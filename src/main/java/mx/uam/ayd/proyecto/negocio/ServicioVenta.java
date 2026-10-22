package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.TrabajadorRepository;
import mx.uam.ayd.proyecto.datos.VentaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.DescripcionVenta;
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

    /**
     * Inicializa una nueva venta (Método requerido por ControlAgregarProductos).
     */
    public Venta iniciarVenta() {
        Venta venta = new Venta();
        venta.setDate(LocalDateTime.now());
        return venta;
    }

    /**
     * Agrega un producto a una venta existente (Método requerido por ControlAgregarProductos).
     */
    public void agregarProducto(Producto producto, int cantidad, Venta venta) {
        if (venta != null && producto != null) {
            venta.agregaProducto(producto, cantidad);
        }
    }

    /**
     * Actualiza o recalcula la venta en memoria (Método requerido por ControlAgregarProductos).
     */
    public Venta actualizarVenta(Venta venta) {
        if (venta != null) {
            venta.calculaTotal();
        }
        return venta;
    }

    /**
     * Registra una venta normal en mostrador.
     */
    @Transactional
    public Venta registrarVenta(List<DescripcionVenta> detalles, double montoRecibido) {
        Venta venta = new Venta();
        venta.setDate(LocalDateTime.now());
        venta.setMontoRecibido(montoRecibido);

        for (DescripcionVenta detalle : detalles) {
            venta.agregarDetalle(detalle);
        }

        double total = venta.getTotal();
        venta.setCambio(montoRecibido - total);

        return ventaRepository.save(venta);
    }

    /**
     * Registra un pedido a domicilio cumpliendo con la HU-17:
     * Valida al cliente, verifica que existan repartidores disponibles (por rol),
     * calcula el total con envío y asigna el repartidor.
     */
    @Transactional
    public Venta registrarPedidoDomicilio(Cliente cliente, List<DescripcionVenta> detalles, double montoRecibido, long idRepartidor) {
        
        // 1. Validar los datos obligatorios del cliente (Escenario 4 de la HU-17)
        if (!servicioCliente.validarDatosCliente(cliente)) {
            throw new IllegalArgumentException("Los datos obligatorios del cliente para domicilio están incompletos.");
        }

        // 2. Verificar disponibilidad de repartidores usando la entidad Trabajador (Escenario 5)
        List<Trabajador> repartidoresDisponibles = trabajadorRepository.findByRol("Repartidor");
        if (repartidoresDisponibles == null || repartidoresDisponibles.isEmpty()) {
            throw new IllegalStateException("No hay repartidores disponibles en este momento. Tiempo de espera estimado: 30 minutos.");
        }

        // 3. Crear y configurar la venta a domicilio
        Venta nuevaVenta = new Venta();
        nuevaVenta.setDate(LocalDateTime.now());
        nuevaVenta.setMontoRecibido(montoRecibido);
        nuevaVenta.setEsDomicilio(true);
        nuevaVenta.setCliente(cliente);

        for (DescripcionVenta detalle : detalles) {
            nuevaVenta.agregarDetalle(detalle);
        }

        // 4. Configurar costo de envío y recalcular totales (Escenario 3)
        double costoEnvio = 50.00; 
        nuevaVenta.setCostoEnvio(costoEnvio);

        double totalFinal = nuevaVenta.getTotal();
        if (montoRecibido < totalFinal) {
            throw new IllegalArgumentException("El monto recibido es menor al total con costo de envío.");
        }
        nuevaVenta.setCambio(montoRecibido - totalFinal);

        // 5. Asignar el repartidor seleccionado (Escenario 1)
        Trabajador repartidor = trabajadorRepository.findById(idRepartidor)
            .orElseThrow(() -> new IllegalArgumentException("El repartidor seleccionado no es válido."));
        
        nuevaVenta.setRepartidorAsignado(repartidor.getNombreCompleto());
        nuevaVenta.setEstadoEntrega("Pedido en camino");

        // 6. Guardar la venta con los datos de domicilio
        return ventaRepository.save(nuevaVenta);
    }

    /**
     * Consulta los pedidos a domicilio activos o en camino para mostrarlos en la tabla (HU-17).
     */
    public List<Venta> obtenerPedidosDomicilioActivos() {
        return ventaRepository.findByEsDomicilioTrueAndEstadoEntregaNot("Entregado");
    }

    /**
     * Marca un pedido como "Entregado" para cerrar el flujo manualmente desde la tabla (HU-17).
     */
    @Transactional
    public void cerrarPedido(long idVenta) {
        Venta venta = ventaRepository.findById(idVenta).orElse(null);
        if (venta != null) {
            venta.setEstadoEntrega("Entregado");
            ventaRepository.save(venta);
        }
    }
}