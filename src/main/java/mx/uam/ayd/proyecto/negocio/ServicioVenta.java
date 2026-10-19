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

    @Autowired
    private ServicioCliente servicioCliente; 
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
        
        Venta nuevaVenta = new Venta();
        nuevaVenta.setFecha(LocalDateTime.now());
        nuevaVenta.setPago(montoRecibido);

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
                    movimiento.setObservacion("Venta realizada");
                    movimiento.setProducto(productoBD);

                    movimientoRepository.save(movimiento);
                    
                    // Agregar a la venta
                    nuevaVenta.agregaProducto(productoBD, cantidadVendida);
                }
            }
        }

        double totalCalculado = nuevaVenta.getTotal() != null ? nuevaVenta.getTotal() : 0.0;
        nuevaVenta.setCambio(montoRecibido - totalCalculado);

        return ventaRepository.save(nuevaVenta);
    }

    /**
     * Registra un pedido a domicilio.
     * @param cliente datos del cliente y su domicilio
     * @param detalles lista de productos
     * @param montoRecibido dinero recibido
     * @return la venta a domicilio guardada
     */
    @Transactional
    public Venta registrarPedidoDomicilio(Cliente cliente, List<DescripcionVenta> detalles, double montoRecibido) {
        
        // hu-17
        if (!servicioCliente.validarDatosCliente(cliente)) {
            throw new IllegalArgumentException("Los datos obligatorios del cliente para domicilio están incompletos.");
        }

        
        Venta nuevaVenta = registrarVenta(detalles, montoRecibido);

        
        nuevaVenta.setEsDomicilio(true);
        nuevaVenta.setCliente(cliente);
        
        double costoEnvio = 50.00; 
        nuevaVenta.setCostoEnvio(costoEnvio);
        
        
        double totalConEnvio = nuevaVenta.getTotal();
        nuevaVenta.setCambio(montoRecibido - totalConEnvio);
        
        nuevaVenta.setEstadoEntrega("Registrado");

        
        return ventaRepository.save(nuevaVenta);
    }

   
    @Transactional
    public void asignarRepartidor(long idVenta, String nombreRepartidor) {
        Venta venta = ventaRepository.findById(idVenta).orElse(null);
        if (venta != null) {
            venta.setRepartidorAsignado(nombreRepartidor);
            venta.setEstadoEntrega("En camino");
            ventaRepository.save(venta);
        }
    }

 
    @Transactional
    public void cerrarPedido(long idVenta) {
        Venta venta = ventaRepository.findById(idVenta).orElse(null);
        if (venta != null) {
            venta.setEstadoEntrega("Entregado");
            ventaRepository.save(venta);
        }
    }

    /**
     * Crea una nueva venta vacía para iniciar una operación de venta.
     *
     * @return una venta nueva y vacía
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