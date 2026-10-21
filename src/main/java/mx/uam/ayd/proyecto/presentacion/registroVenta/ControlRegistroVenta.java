package mx.uam.ayd.proyecto.presentacion.registroVenta;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioVenta;
import mx.uam.ayd.proyecto.negocio.modelo.DescripcionVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;
import mx.uam.ayd.proyecto.presentacion.Cliente.ControlCliente;

/**
 * Controlador de la HU-04 para gestionar el flujo del carrito de compras, el cobro y los pedidos a domicilio.
 * 
 * @author kevin dydier
 */
@Component
public class ControlRegistroVenta {

    // Dependencias
    private final ServicioVenta servicioVenta;
    private final VentanaCarrito ventanaCarrito;
    private final VentanaCobro ventanaCobro;
    
    // NUEVA DEPENDENCIA para gestionar el flujo de clientes y domicilio (HU-17)
    private final ControlCliente controlCliente;

    // Estado del proceso: Guardamos directamente la Venta
    private Venta ventaActual;

    /**
     * Constructor donde Spring inyecta el servicio de ventas, las vistas y el control de cliente.
     * 
     * @param servicioVenta servicio con la lógica de negocio
     * @param v1 vista del carrito de compras
     * @param v2 vista para cobrar y dar cambio
     * @param controlCliente control para la gestión de datos del cliente
     */
    @Autowired
    public ControlRegistroVenta(ServicioVenta servicioVenta, VentanaCarrito v1, VentanaCobro v2, ControlCliente controlCliente) {
        this.servicioVenta = servicioVenta;
        this.ventanaCarrito = v1;
        this.ventanaCobro = v2;
        this.controlCliente = controlCliente;
    }

    /**
     * Se ejecuta después de crear el componente para conectar las ventanas con este controlador
     */
    @PostConstruct
    public void init() {
        if (ventanaCarrito != null) ventanaCarrito.setControl(this);
        if (ventanaCobro != null) ventanaCobro.setControl(this);
    }

    /**
     * Recibe la venta completa creada en la HU de selección de productos.
     * 
     * @param venta objeto venta con la lista de productos acumulados
     */
    public void iniciaConVenta(Venta venta) {
        this.ventaActual = venta;
        
        if (venta != null) {
            System.out.println("Productos recibidos: " + (venta.getProductos() != null ? venta.getProductos().size() : 0));
            System.out.println("Total recibido: $" + venta.getTotal());
        } else {
            System.out.println("Objeto Venta recibido es NULL");
        }

        if (ventanaCarrito != null) {
            ventanaCarrito.muestra(this.ventaActual);
        }
    }

    /**
     * Método para abrir el formulario de cliente y domicilio (Pedidos a Domicilio / HU-17)
     */
    public void iniciarPedidoADomicilio() {
        if (controlCliente != null) {
            controlCliente.inicia(); // Esto abre la ventana VentanaCliente.fxml con sus respectivos botones
        }
    }

    /**
     * Valida reglas de negocio antes de permitir el cobro.
     */
    public void procesarConfirmacionVenta() {
        if (ventaActual == null || ventaActual.getProductos() == null || ventaActual.getProductos().isEmpty()) {
            if (ventanaCarrito != null) {
                ventanaCarrito.muestraDialogoConMensaje("El carrito de compras está vacío.");
            }
            return;
        }

        // RN-04: Validar precios antes de habilitar pantalla de cobro
        for (DescripcionVenta d : ventaActual.getProductos()) {
            if (d != null && d.getPrecioUnitario() <= 0) {
                if (ventanaCarrito != null) {
                    ventanaCarrito.muestraDialogoConMensaje("RN-04: Todo precio asignado debe ser mayor a cero.");
                }
                return;
            }
        }

        // Ocultar carrito y mostrar pantalla de cobro
        if (ventanaCarrito != null) {
            ventanaCarrito.setVisible(false); // Oculta la ventana de Carrito
        }

        if (ventanaCobro != null) {
            ventanaCobro.muestra(ventaActual.getTotal()); // Abre la ventana de Cobro con el total
        }
    }

    /**
     * Revisa si el dinero que entregó el cliente alcanza para la venta y calcula el cambio.
     * 
     * @param efectivoRecibido dinero en efectivo ingresado por el usuario
     */
    public void calcularCambio(double efectivoRecibido) {
        if (ventanaCobro == null || ventaActual == null) return;

        double totalVenta = ventaActual.getTotal();

        if (efectivoRecibido < totalVenta) {
            ventanaCobro.actualizaCambio(0, "Efectivo insuficiente");
        } else {
            double cambio = efectivoRecibido - totalVenta;
            ventanaCobro.actualizaCambio(cambio, null);
        }
    }

    /**
     * Finaliza la compra pasando la Venta al servicio
     * 
     * @param efectivoRecibido dinero en efectivo entregado para procesar el pago
     */
    public void finalizarCompra(double efectivoRecibido) {
        if (servicioVenta == null || ventanaCobro == null || ventaActual == null) return;

        try {
            servicioVenta.registrarVenta(ventaActual.getProductos(), efectivoRecibido);
            
            ventanaCobro.muestraDialogoConMensaje("La venta ha sido exitosa.");
            termina();
            
        } catch (Exception ex) {
            String mensajeError = (ex.getMessage() != null) ? ex.getMessage() : "Error en el registro de venta.";
            ventanaCobro.muestraDialogoConMensaje("Error al registrar: " + mensajeError);
        }
    }

    /**
     * Oculta las dos pantallas para cerrar o reiniciar la transacción
     */
    public void termina() {
        if (ventanaCobro != null) ventanaCobro.setVisible(false);
        if (ventanaCarrito != null) ventanaCarrito.setVisible(false);
    }
}