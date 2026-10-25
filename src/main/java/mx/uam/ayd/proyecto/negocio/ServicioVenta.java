package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.TrabajadorRepository;
import mx.uam.ayd.proyecto.datos.VentaRepository;
import mx.uam.ayd.proyecto.datos.MovimientoInventarioRepository;
import mx.uam.ayd.proyecto.datos.InventarioRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.DescripcionVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Inventario;
import mx.uam.ayd.proyecto.negocio.modelo.MovimientoInventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Trabajador;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

/**
 * Servicio encargado de la lógica de negocio de las ventas,
 * incluyendo el carrito, ventas en mostrador y los pedidos a domicilio (HU-17).
 */
@Service
public class ServicioVenta {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ServicioCliente servicioCliente;

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

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
                    
                    // 1. ACTUALIZAR DIRECTAMENTE LA EXISTENCIA EN EL PRODUCTO
                    productoBD.setExistenciaActual(nuevaExistencia);
                    productoRepository.save(productoBD);

                    // 2. ACTUALIZAR EN INVENTARIO (Para que se dispare la Alerta de Stock)
                    Inventario inventario = inventarioRepository.findById(productoBD.getIdProducto()).orElse(null);
                    if (inventario != null) {
                        inventario.setExistenciaActual(nuevaExistencia);
                        inventarioRepository.save(inventario);
                    }
                    
                    // 3. Registrar salida en Historial de Movimientos
                    MovimientoInventario movimiento = new MovimientoInventario();
                    movimiento.setFecha(LocalDateTime.now());
                    movimiento.setTipoMovimiento("SALIDA");
                    movimiento.setCantidad(cantidadVendida);
                    movimiento.setExistenciaAnterior(existenciaAnterior);
                    movimiento.setExistenciaActual(nuevaExistencia);
                    movimiento.setObservacion("Venta realizada");
                    movimiento.setProducto(productoBD);

                    movimientoRepository.save(movimiento);
                    
                    // 4. Agregar a la venta
                    nuevaVenta.agregaProducto(productoBD, cantidadVendida);
                }
            }
        }

        double totalCalculado = nuevaVenta.getTotal() != null ? nuevaVenta.getTotal() : 0.0;
        nuevaVenta.setCambio(montoRecibido - totalCalculado);

        return ventaRepository.save(nuevaVenta);
    }
    /**
     * Registra un pedido a domicilio cumpliendo con la HU-17 (Validación de cliente, repartidores y envío).
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
        venta.setCostoEnvio(50.0); // Costo de envío estándar configurable (Escenario 3)
        venta.setCliente(cliente);

        if (detalles != null) {
            for (DescripcionVenta detalle : detalles) {
                venta.agregaProducto(detalle.getProducto(), detalle.getCantidad());
            }
        }

        double totalConEnvio = venta.getTotal(); // Incluye subtotal de productos + costo de envío
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
        
        venta.setRepartidorAsignado(repartidorSeleccionado.getNombreCompleto());
        venta.setEstadoEntrega("Pedido en camino");

        return ventaRepository.save(venta);
    }

    /**
     * Obtiene una lista de las ventas que son pedidos a domicilio y que siguen activos/pendientes.
     */
    public List<Venta> obtenerPedidosDomicilioActivos() {
        List<Venta> todasLasVentas = (List<Venta>) ventaRepository.findAll();
        List<Venta> pedidosActivos = new ArrayList<>();
        
        for (Venta v : todasLasVentas) {
            if (v.isEsDomicilio() && !"Entregado".equalsIgnoreCase(v.getEstadoEntrega())) {
                pedidosActivos.add(v);
            }
        }
        return pedidosActivos;
    }

    /**
     * Cierra o marca como entregado un pedido a domicilio por su ID.
     */
    @Transactional
    public boolean cerrarPedido(long idVenta) {
        Optional<Venta> ventaOpt = ventaRepository.findById(idVenta);
        if (ventaOpt.isPresent()) {
            Venta venta = ventaOpt.get();
            venta.setEstadoEntrega("Entregado");
            ventaRepository.save(venta);
            return true;
        }
        return false;
    }
}