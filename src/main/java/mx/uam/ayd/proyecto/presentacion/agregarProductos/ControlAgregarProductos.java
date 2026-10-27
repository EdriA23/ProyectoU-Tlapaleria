package mx.uam.ayd.proyecto.presentacion.agregarProductos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.negocio.ServicioVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;
import mx.uam.ayd.proyecto.presentacion.registroVenta.ControlRegistroVenta;
import mx.uam.ayd.proyecto.presentacion.seleccionarCliente.ControlSeleccionarCliente;

/**
 * Controlador principal para la HU-05 (Agregar productos a una venta).
 * Se encarga de coordinar la interfaz grafica con la logica de negocio.
 */
@Component
public class ControlAgregarProductos {

    @Autowired
    private ServicioProducto servicioProducto;

    @Autowired
    private ServicioVenta servicioVenta;

    @Autowired
    private ControlSeleccionarCliente controlSeleccionarCliente;

    @Autowired
    private VistaAgregarProductos vistaAgregarProductos;

    @Autowired
    @Lazy
    private ControlRegistroVenta controlRegistroVenta;

    private Venta ventaActual;

    // Cliente registrado que realiza la compra (null = público en general)
    private Cliente clienteSeleccionado;

    /**
     * Inicia el flujo de la HU-05 cargando la lista de productos
     * y mostrando la ventana principal.
     */
    public void inicia() {
        Iterable<Producto> productos = servicioProducto.recuperaProductos();
        vistaAgregarProductos.mostrarVentanaVenta(this, productos);
    }

    /**
     * Crea una nueva venta vacia para comenzar a agregar productos (HU-05).
     * También limpia el cliente seleccionado de la venta anterior.
     */
    public void iniciarVenta() {
        this.ventaActual = servicioVenta.iniciarVenta();
        this.clienteSeleccionado = null;
    }

    /**
     * Agrega un producto a la venta actual tras validar que haya inventario (HU-05).
     * Muestra la venta actualizada o un mensaje si no hay stock suficiente.
     * 
     * @param producto producto seleccionado
     * @param cantidad cantidad de piezas a agregar
     */
    public void agregarProductos(Producto producto, int cantidad) {
        // Validamos si hay existencias
        boolean disponible = servicioProducto.verificaDisponibilidad(producto, cantidad);

        if (disponible) {
            // Agregamos y guardamos los cambios
            this.ventaActual = servicioVenta.agregarProducto(producto, cantidad, this.ventaActual);
            servicioVenta.actualizarVenta(this.ventaActual);
            
            // Actualizamos la tabla/carrito en pantalla
            vistaAgregarProductos.mostrarVenta(this.ventaActual);
        } else {
            vistaAgregarProductos.muestraMensajeError("No hay inventario suficiente para el producto: " + producto.getNombre());
        }
    }

    /**
     * Abre el diálogo para buscar y seleccionar un cliente ya registrado.
     */
    public void buscarCliente() {
        controlSeleccionarCliente.inicia(this::seleccionarCliente);
    }

    /**
     * Asocia el cliente elegido a la venta en curso y lo muestra en pantalla.
     *
     * @param cliente cliente seleccionado (null para quitar el cliente)
     */
    public void seleccionarCliente(Cliente cliente) {
        this.clienteSeleccionado = cliente;
        if (ventaActual != null) {
            ventaActual.setCliente(cliente);
        }
        vistaAgregarProductos.muestraCliente(cliente);
    }

    /**
     * Quita el cliente de la venta (queda como público en general).
     */
    public void quitarCliente() {
        seleccionarCliente(null);
    }

    /**
     * Valida que la venta tenga productos agregados (HU-05) antes de
     * pasar el control al siguiente modulo de cobro/registro.
     */
    public void continuarRegistroVenta() {
        if (this.ventaActual == null || 
            this.ventaActual.getProductos() == null || 
            this.ventaActual.getProductos().isEmpty()) {
            
            vistaAgregarProductos.muestraMensajeError("Debes agregar al menos un producto a la compra para poder continuar.");
            return;
        }

        // Aseguramos que la venta lleve el cliente seleccionado (o ninguno)
        this.ventaActual.setCliente(this.clienteSeleccionado);

        // Oculta esta ventana y le pasa la venta activa al siguiente flujo
        vistaAgregarProductos.setVisible(false);

        Platform.runLater(() -> {
            controlRegistroVenta.iniciaConVenta(this.ventaActual);
        });
    }
}