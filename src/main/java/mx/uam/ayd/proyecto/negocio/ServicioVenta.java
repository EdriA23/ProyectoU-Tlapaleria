package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
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

    @Autowired
    private CrudRepository<Cliente, Long> clienteRepository;

    /**
     * Inicializa una nueva venta (Método requerido por ControlAgregarProductos).
     */
    public Venta iniciarVenta() {
        Venta venta = new Venta();
        venta.setDate(LocalDateTime.now());
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
     * Registra un pedido a domicilio cumpliendo con la HU-17 de forma segura con JPA.
     */
    @Transactional
    public Venta registrarPedidoDomicilio(Cliente cliente, List<DescripcionVenta> detalles, double montoRecibido, long idRepartidor) {
        
        // 1. Validar los datos obligatorios del cliente (Escenario 4 de la HU-17)
        if (!servicioCliente.validarDatosCliente(cliente)) {
            throw new IllegalArgumentException("Los datos obligatorios del cliente para domicilio están incompletos.");
        }

        // 2. Buscar o asegurar un repartidor disponible
        List<Trabajador> repartidoresDisponibles = trabajadorRepository.findByRol("Repartidor");
        Trabajador repartidorSeleccionado = null;

        if (repartidoresDisponibles == null || repartidoresDisponibles.isEmpty()) {
            repartidorSeleccionado = new Trabajador();
            repartidorSeleccionado.setNombreCompleto("Repartidor General");
            repartidorSeleccionado.setRol("Repartidor");
            repartidorSeleccionado.setTelefono("5550000000");
            repartidorSeleccionado.setNumeroTrabajador("REP-DEF");
            repartidorSeleccionado = trabajadorRepository.save(repartidorSeleccionado);
        } else {
            repartidorSeleccionado = repartidoresDisponibles.get(0);
        }

        // 3. Asegurar que el cliente esté guardado o fusionado correctamente en la BD
        Cliente clienteGestionado = clienteRepository.save(cliente);

        // 4. Crear y configurar la venta a domicilio
        Venta nuevaVenta = new Venta();
        nuevaVenta.setDate(LocalDateTime.now());
        nuevaVenta.setEsDomicilio(true);
        nuevaVenta.setCliente(clienteGestionado);

        // Asociar cada detalle de manera limpia evitando entidades detached
        if (detalles != null) {
            for (DescripcionVenta detalle : detalles) {
                DescripcionVenta nuevoDetalle = new DescripcionVenta();
                nuevoDetalle.setProducto(detalle.getProducto());
                nuevoDetalle.setCantidad(detalle.getCantidad());
                nuevaVenta.agregarDetalle(nuevoDetalle);
            }
        }

        // 5. Configurar costo de envío y recalcular totales (Escenario 3)
        double costoEnvio = 50.00; 
        nuevaVenta.setCostoEnvio(costoEnvio);

        double totalFinal = nuevaVenta.getTotal();

        if (montoRecibido < totalFinal) {
            montoRecibido = totalFinal; 
        }

        nuevaVenta.setMontoRecibido(montoRecibido);
        nuevaVenta.setCambio(montoRecibido - totalFinal);

        // 6. Asignar el repartidor asegurado y estado inicial
        nuevaVenta.setRepartidorAsignado(repartidorSeleccionado.getNombreCompleto());
        nuevaVenta.setEstadoEntrega("Registrado");

        // 7. Guardar la venta de forma segura usando merge/save
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