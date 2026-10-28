package mx.uam.ayd.proyecto.presentacion.registroVenta;

import java.util.Collections;
import java.util.List;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioVenta;
import mx.uam.ayd.proyecto.negocio.ServicioTarjeta;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Tarjeta;
import mx.uam.ayd.proyecto.negocio.modelo.DescripcionVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;
import mx.uam.ayd.proyecto.presentacion.Cliente.ControlCliente;
import mx.uam.ayd.proyecto.presentacion.AgregarTarjeta.ControlAgregarTarjeta;

/**
 * Controlador de la HU-04 para gestionar el flujo del carrito de compras, el cobro y los pedidos a domicilio.
 * 
 * @author kevin dydier
 */
@Component
public class ControlRegistroVenta {

    // Dependencias
    private final ServicioVenta servicioVenta;
    private final ServicioTarjeta servicioTarjeta;
    private final VentanaCarrito ventanaCarrito;
    private final VentanaCobro ventanaCobro;
    
    // Dependencia para gestionar el flujo de clientes y domicilio (HU-17)
    private final ControlCliente controlCliente;
    
    // Dependencia para abrir la ventana de agregar tarjetas
    private final ControlAgregarTarjeta controlAgregarTarjeta;

    // Estado del proceso: Guardamos directamente la Venta
    private Venta ventaActual;

    /**
     * Constructor donde Spring inyecta el servicio de ventas, tarjetas, las vistas y controles necesarios.
     */
    @Autowired
    public ControlRegistroVenta(ServicioVenta servicioVenta, 
                                ServicioTarjeta servicioTarjeta,
                                VentanaCarrito v1, 
                                VentanaCobro v2, 
                                ControlCliente controlCliente,
                                ControlAgregarTarjeta controlAgregarTarjeta) {
        this.servicioVenta = servicioVenta;
        this.servicioTarjeta = servicioTarjeta;
        this.ventanaCarrito = v1;
        this.ventanaCobro = v2;
        this.controlCliente = controlCliente;
        this.controlAgregarTarjeta = controlAgregarTarjeta;
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
     * Obtiene las tarjetas asociadas a un cliente.
     * Llamado desde VentanaCarrito al momento de cargar o actualizar los datos.
     */
    public List<Tarjeta> obtenerTarjetasDelCliente(Cliente cliente) {
        if (cliente == null || servicioTarjeta == null) {
            return Collections.emptyList();
        }
        return servicioTarjeta.obtenerTarjetasPorCliente(cliente);
    }

    /**
     * Abre el flujo para agregar una nueva tarjeta. 
     * Funciona tanto para un cliente registrado como para Venta al Público General (cliente == null).
     */
    public void abrirVentanaAgregarTarjeta(Cliente cliente, VentanaCarrito vistaOrigen) {
        if (controlAgregarTarjeta != null) {
            controlAgregarTarjeta.inicia(cliente, (Tarjeta tarjetaCapturada) -> {
                
                // 1. Asociamos la tarjeta a la venta activa
                if (this.ventaActual != null && tarjetaCapturada != null) {
                    this.ventaActual.setTarjeta(tarjetaCapturada);
                    this.ventaActual.setTipoPago("TARJETA");
                }
                
                // 2. Si existe un cliente registrado, agregamos la tarjeta a su expediente
                if (cliente != null && tarjetaCapturada != null) {
                    cliente.agregarTarjeta(tarjetaCapturada);
                }

                // 3. Notificamos a la vista para renderizar la tarjeta en el ListView
                if (vistaOrigen != null) {
                    vistaOrigen.actualizarDatosClienteYTarjetas();
                }
            });
        }
    }

    public void iniciarPedidoADomicilio() {
        if (controlCliente != null && ventaActual != null) {
            controlCliente.iniciaConVenta(ventaActual);
        } else if (controlCliente != null) {
            controlCliente.inicia();
        }
    }

    /**
     * Valida reglas de negocio antes de permitir el cobro.
     * Si el pago es con TARJETA, finaliza la venta directamente sin abrir la pantalla de efectivo/cambio.
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

        // --- BIFURCACIÓN SEGÚN EL TIPO DE PAGO ---
        if ("TARJETA".equalsIgnoreCase(ventaActual.getTipoPago())) {
            
            // Validar que realmente se haya seleccionado/agregado una tarjeta
            if (ventaActual.getTarjeta() == null) {
                if (ventanaCarrito != null) {
                    ventanaCarrito.muestraDialogoConMensaje("Debe agregar o seleccionar una tarjeta antes de continuar.");
                }
                return;
            }

            // Ocultamos la ventana del carrito
            if (ventanaCarrito != null) {
                ventanaCarrito.setVisible(false);
            }

            // Para tarjetas se cobra el monto exacto y el cambio es 0.0
            double totalVenta = ventaActual.getTotal();
            finalizarCompra(totalVenta);

        } else {
            // PAGO EN EFECTIVO: Mostramos la pantalla de cobro para ingresar monto y calcular cambio
            if (ventanaCarrito != null) {
                ventanaCarrito.setVisible(false);
            }

            if (ventanaCobro != null) {
                ventanaCobro.muestra(ventaActual.getTotal());
            }
        }
    }

    /**
     * Revisa si el dinero que entregó el cliente alcanza para la venta y calcula el cambio.
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
     * Finaliza la compra pasando la Venta al servicio.
     * Soporta pagos en EFECTIVO y TARJETA, con o sin cliente registrado.
     */
    public void finalizarCompra(double efectivoRecibido) {
        if (servicioVenta == null || ventaActual == null) return;

        try {
            String tipoPago = (ventaActual.getTipoPago() != null) ? ventaActual.getTipoPago() : "EFECTIVO";
            
            servicioVenta.registrarVenta(
                ventaActual.getProductos(), 
                efectivoRecibido, 
                tipoPago, 
                ventaActual.getCliente(), 
                ventaActual.getTarjeta()
            );
            
            if (ventanaCarrito != null) {
                ventanaCarrito.muestraDialogoConMensaje("La venta ha sido exitosa.");
            } else if (ventanaCobro != null) {
                ventanaCobro.muestraDialogoConMensaje("La venta ha sido exitosa.");
            }
            
            // Reiniciamos la venta actual a null para permitir nuevas transacciones
            this.ventaActual = null;
            
            termina();
            
        } catch (Exception ex) {
            String mensajeError = (ex.getMessage() != null) ? ex.getMessage() : "Error en el registro de venta.";
            if (ventanaCobro != null) {
                ventanaCobro.muestraDialogoConMensaje("Error al registrar: " + mensajeError);
            } else if (ventanaCarrito != null) {
                ventanaCarrito.muestraDialogoConMensaje("Error al registrar: " + mensajeError);
            }
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