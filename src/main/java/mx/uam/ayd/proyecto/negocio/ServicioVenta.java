package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.InventarioRepository;
import mx.uam.ayd.proyecto.datos.MovimientoInventarioRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.datos.TrabajadorRepository;
import mx.uam.ayd.proyecto.datos.VentaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.DescripcionVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Inventario;
import mx.uam.ayd.proyecto.negocio.modelo.MovimientoInventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Tarjeta;
import mx.uam.ayd.proyecto.negocio.modelo.Trabajador;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

/**
 * Servicio principal para procesar la lógica de las ventas del carrito,
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
    private TrabajadorRepository trabajadorRepository;

    @Autowired
    private ServicioCliente servicioCliente;

    /**
     * Inicializa una nueva venta vacía.
     */
    public Venta iniciarVenta() {
        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        return venta;
    }

    /**
     * Agrega un producto a una venta existente y devuelve la venta actualizada.
     */
    public Venta agregarProducto(Producto producto, int cantidad, Venta venta) {
        if (venta == null) {
            venta = iniciarVenta();
        }
        if (producto != null) {
            venta.agregaProducto(producto, cantidad);
        }
        return venta;
    }

    /**
     * Actualiza y recalcula la venta en memoria (Escenario 3: Recálculo automático por cambios en productos o costos).
     */
    @Transactional
    public Venta actualizarVenta(Venta venta, double montoRecibido) {
        if (venta == null) {
            return null;
        }
        
        double totalFinal = venta.getTotal(); // El método getTotal() en Venta ya calcula productos + costo de envío si aplica
        venta.setPago(montoRecibido);
        venta.setCambio(montoRecibido - totalFinal);

        return ventaRepository.save(venta);
    }

    /**
     * Sobrecarga para actualizar venta básica si no se pasa monto recibido de inmediato.
     */
    public Venta actualizarVenta(Venta venta) {
        if (venta != null) {
            ventaRepository.save(venta);
        }
        return venta;
    }

    /**
     * Hace todo el proceso de la venta en efectivo y sin cliente: descuenta existencias
     * en producto e inventario, genera la salida en el historial de movimientos y
     * calcula el cambio del cliente.
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
     * Registra una venta en efectivo asociándola al cliente seleccionado en la pantalla de ventas.
     * 
     * @param detalles lista de productos y cantidades compradas
     * @param montoRecibido dinero en efectivo que entrego el cliente
     * @param cliente cliente registrado que realiza la compra (puede ser null = público en general)
     * @return la venta guardada en la base de datos
     */
    @Transactional
    public Venta registrarVenta(List<DescripcionVenta> detalles, double montoRecibido, Cliente cliente) {
        return registrarVenta(detalles, montoRecibido, "EFECTIVO", cliente, null);
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
                cliente.agregarTarjeta(tarjeta); // Corregido: se usa agregarTarjeta en lugar de setTarjeta
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

        // 1. Validar los datos obligatorios del cliente (Escenario 4 de la HU-17)
        if (!servicioCliente.validarDatosCliente(cliente)) {
            throw new IllegalArgumentException("Los datos obligatorios del cliente para domicilio están incompletos.");
        }

        // 2. Verificar disponibilidad de repartidores (Escenario 5)
        List<Trabajador> repartidoresDisponibles = trabajadorRepository.findByRol("Repartidor");
        if (repartidoresDisponibles == null || repartidoresDisponibles.isEmpty()) {
            throw new IllegalStateException("No hay repartidores disponibles. Tiempo de espera estimado: 30 minutos.");
        }

        // 3. Crear y configurar la venta a domicilio
        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setEsDomicilio(true);
        double costoEnvio = 50.0; // Costo de envío estándar configurable (Escenario 3)
        venta.setCliente(cliente);

        if (detalles != null) {
            for (DescripcionVenta detalle : detalles) {
                venta.agregaProducto(detalle.getProducto(), detalle.getCantidad());
            }
        }

        double totalConEnvio = venta.getTotal() + costoEnvio;
        venta.setPago(montoRecibido);
        venta.setCambio(montoRecibido - totalConEnvio);

        // Asignar el repartidor seleccionado (si pasas un ID genérico y hay repartidores, asignamos el primero disponible o por ID)
        Trabajador repartidorSeleccionado;
        if (idRepartidor <= 0 && !repartidoresDisponibles.isEmpty()) {
            repartidorSeleccionado = repartidoresDisponibles.get(0);
        } else {
            repartidorSeleccionado = trabajadorRepository.findById(idRepartidor)
                    .orElse(repartidoresDisponibles.get(0)); // Respaldo por si el ID directo no cuadra pero sí hay repartidores
        }
        
        venta.setEstadoEntrega("Pedido en camino");

        return ventaRepository.save(venta);
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

}