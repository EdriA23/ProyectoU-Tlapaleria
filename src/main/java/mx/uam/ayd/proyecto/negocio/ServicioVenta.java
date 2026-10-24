package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.InventarioRepository;
import mx.uam.ayd.proyecto.datos.MovimientoInventarioRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.datos.VentaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.DescripcionVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Inventario;
import mx.uam.ayd.proyecto.negocio.modelo.MovimientoInventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Tarjeta;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

/**
 * Servicio principal para procesar la logica de las ventas del carrito,
 * descuenta el stock de las tablas y guarda los movimientos en la bd.
 *
 * @author dydier
 */
@Service
public class ServicioVenta {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    /**
     * Hace todo el proceso de la venta: descuenta existencias en producto e inventario,
     * genera la salida en el historial de movimientos y calcula el cambio del cliente.
     * 
     * @param detalles lista de productos y cantidades compradas
     * @param montoRecibido dinero en efectivo que entrego el cliente
     * @return la venta guardada en la base de datos
     */
    @Transactional
    public Venta registrarVenta(List<DescripcionVenta> detalles, double montoRecibido) {
        return registrarVenta(detalles, montoRecibido, "EFECTIVO", null, null);
    }

    /**
     * Registra una venta permitiendo especificar el metodo de pago (EFECTIVO o TARJETA),
     * el cliente opcional y la tarjeta opcional.
     * 
     * @param detalles lista de productos y cantidades compradas
     * @param montoRecibido dinero entregado o monto cobrado
     * @param tipoPago tipo de pago ("EFECTIVO" o "TARJETA")
     * @param cliente cliente que realiza la compra (puede ser null)
     * @param tarjeta tarjeta con la que se efectua el pago (puede ser null si es efectivo)
     * @return la venta guardada en la base de datos
     */
    @Transactional
    public Venta registrarVenta(List<DescripcionVenta> detalles, double montoRecibido, String tipoPago, Cliente cliente, Tarjeta tarjeta) {
        
        Venta nuevaVenta = new Venta();
        nuevaVenta.setFecha(LocalDateTime.now());
        nuevaVenta.setPago(montoRecibido);
        nuevaVenta.setTipoPago(tipoPago != null ? tipoPago : "EFECTIVO");
        nuevaVenta.setCliente(cliente);

        if ("TARJETA".equalsIgnoreCase(tipoPago) && tarjeta != null) {
            nuevaVenta.setTarjeta(tarjeta);
            if (cliente != null) {
                cliente.setTarjeta(tarjeta);
            }
        }

        if (detalles != null) {
            for (DescripcionVenta detalle : detalles) {
                Producto productoBD = detalle.getProducto();
                
                if (productoBD != null) {
                    int existenciaAnterior = productoBD.getExistenciaActual();
                    int cantidadVendida = detalle.getCantidad();
                    int nuevaExistencia = existenciaAnterior - cantidadVendida;
                    
                    // Actualizar el stock en Producto
                    productoBD.setExistenciaActual(nuevaExistencia);
                    productoRepository.save(productoBD);

                    // ACTUALIZAR EN INVENTARIO (Para que se dispare la Alerta de Stock)
                    Inventario inventario = inventarioRepository.findById(productoBD.getIdProducto()).orElse(null);
                    if (inventario != null) {
                        inventario.setExistenciaActual(nuevaExistencia);
                        inventarioRepository.save(inventario);
                    }
                    
                    // Registrar salida en Historial de Movimientos
                    MovimientoInventario movimiento = new MovimientoInventario();
                    movimiento.setFecha(LocalDateTime.now());
                    movimiento.setTipoMovimiento("SALIDA");
                    movimiento.setCantidad(cantidadVendida);
                    movimiento.setExistenciaAnterior(existenciaAnterior);
                    movimiento.setExistenciaActual(nuevaExistencia);
                    movimiento.setObservacion("Venta realizada (" + nuevaVenta.getTipoPago() + ")");
                    movimiento.setProducto(productoBD);

                    movimientoRepository.save(movimiento);
                    
                    // Agregar a la venta
                    nuevaVenta.agregaProducto(productoBD, cantidadVendida);
                }
            }
        }

        double totalCalculado = nuevaVenta.getTotal() != null ? nuevaVenta.getTotal() : 0.0;
        
        if ("TARJETA".equalsIgnoreCase(tipoPago)) {
            nuevaVenta.setCambio(0.0);
        } else {
            nuevaVenta.setCambio(montoRecibido - totalCalculado);
        }

        return ventaRepository.save(nuevaVenta);
    }

    /**
     * Registra un pedido a domicilio asociando el cliente y la lista de productos.
     */
    @Transactional
    public Venta registrarPedidoDomicilio(Cliente cliente, List<DescripcionVenta> detalles, double montoRecibido, long idRepartidor) {
        Venta ventaDomicilio = registrarVenta(detalles, montoRecibido, "EFECTIVO", cliente, null);
        ventaDomicilio.setEsDomicilio(true);
        ventaDomicilio.setEstadoEntrega("PENDIENTE");
        return ventaRepository.save(ventaDomicilio);
    }

    /**
     * Obtiene la lista de pedidos a domicilio que se encuentran activos/pendientes.
     */
    public List<Venta> obtenerPedidosDomicilioActivos() {
        return ventaRepository.findByEsDomicilioTrueAndEstadoEntregaNot("ENTREGADO");
    }

    /**
     * Cierra o finaliza un pedido a domicilio por su ID.
     */
    @Transactional
    public boolean cerrarPedido(long idVenta) {
        Venta venta = ventaRepository.findById(idVenta).orElse(null);
        if (venta != null) {
            venta.setEstadoEntrega("ENTREGADO");
            ventaRepository.save(venta);
            return true;
        }
        return false;
    }

    /**
     * Inicia una nueva venta en blanco para comenzar el flujo de la HU-05.
     * 
     * @return una nueva instancia de Venta vacia
     */
    public Venta iniciarVenta() {
        return new Venta();
    }

    /**
     * Agrega un producto a la venta (HU-05). Si la venta no existe
     * todavia, crea una nueva antes de agregar el producto.
     * 
     * @param producto producto a vender
     * @param cantidad piezas elegidas
     * @param venta la venta actual
     * @return la venta con el producto ya agregado
     */
    public Venta agregarProducto(Producto producto, int cantidad, Venta venta) {
        if (venta == null) {
            venta = iniciarVenta();
        }
        venta.agregaProducto(producto, cantidad);
        return venta;
    }

    /**
     * Guarda el estado actual de la venta en la base de datos (HU-05).
     * 
     * @param venta la venta que se quiere actualizar
     * @return true si se guardo con exito, false si la venta era nula
     */
    public boolean actualizarVenta(Venta venta) {
        if (venta == null) {
            return false;
        }
        ventaRepository.save(venta);
        return true;
    }
}