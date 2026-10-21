package mx.uam.ayd.proyecto.presentacion.registrarCliente;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioCliente;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

/**
 * Controlador de la HU-12 (Registrar clientes).
 * Coordina la ventana de registro con el servicio de clientes.
 */
@Component
public class ControlRegistrarCliente {

    private static final Logger log = LoggerFactory.getLogger(ControlRegistrarCliente.class);

    private final ServicioCliente servicioCliente;
    private final VentanaRegistrarCliente ventana;

    @Autowired
    public ControlRegistrarCliente(ServicioCliente servicioCliente, VentanaRegistrarCliente ventana) {
        this.servicioCliente = servicioCliente;
        this.ventana = ventana;
    }

    /**
     * Conecta la ventana con este controlador después de crear el bean.
     */
    @PostConstruct
    public void init() {
        ventana.setControl(this);
    }

    /**
     * Inicia el flujo de la HU-12 mostrando el formulario de registro.
     */
    public void inicia() {
        log.info("Iniciando HU-12: Registrar clientes");
        ventana.muestra();
    }

    /**
     * Solicita el registro del cliente y muestra el resultado al usuario.
     *
     * @param nombre nombre completo
     * @param telefono teléfono
     * @param correo correo electrónico (opcional)
     * @param direccion dirección (opcional)
     * @return true si el cliente se registró, false en caso contrario
     */
    public boolean registrarCliente(String nombre, String telefono, String correo, String direccion) {
        try {
            Cliente cliente = servicioCliente.registrarCliente(nombre, telefono, correo, direccion);
            ventana.muestraDialogoConMensaje(
                    "Cliente registrado exitosamente con descuento de fidelidad asignado\n"
                    + "Número de cliente: " + cliente.getIdCliente());
            return true;
        } catch (IllegalArgumentException ex) {
            ventana.muestraError(ex.getMessage());
            return false;
        } catch (Exception ex) {
            log.error("Error inesperado al registrar cliente", ex);
            ventana.muestraError("Ocurrió un error inesperado al registrar el cliente.");
            return false;
        }
    }

    /**
     * Cierra la ventana de registro.
     */
    public void termina() {
        ventana.setVisible(false);
    }
}