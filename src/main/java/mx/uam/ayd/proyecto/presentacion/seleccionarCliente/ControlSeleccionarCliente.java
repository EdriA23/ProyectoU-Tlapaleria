package mx.uam.ayd.proyecto.presentacion.seleccionarCliente;

import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioCliente;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

/**
 * Controlador del diálogo para buscar y seleccionar un cliente ya registrado
 * durante el proceso de venta.
 */
@Component
public class ControlSeleccionarCliente {

    private static final Logger log = LoggerFactory.getLogger(ControlSeleccionarCliente.class);

    private final ServicioCliente servicioCliente;
    private final VentanaSeleccionarCliente ventana;

    /** Acción que se ejecuta cuando el usuario elige un cliente. */
    private Consumer<Cliente> alSeleccionar;

    @Autowired
    public ControlSeleccionarCliente(ServicioCliente servicioCliente, VentanaSeleccionarCliente ventana) {
        this.servicioCliente = servicioCliente;
        this.ventana = ventana;
    }

    @PostConstruct
    public void init() {
        ventana.setControl(this);
    }

    /**
     * Abre el diálogo y carga todos los clientes registrados.
     *
     * @param alSeleccionar acción a ejecutar con el cliente elegido
     */
    public void inicia(Consumer<Cliente> alSeleccionar) {
        this.alSeleccionar = alSeleccionar;
        ventana.muestra();
        cargaTodos();
    }

    /**
     * Muestra todos los clientes registrados.
     */
    public void cargaTodos() {
        try {
            ventana.muestraClientes(servicioCliente.recuperaClientes());
        } catch (Exception ex) {
            log.error("Error al recuperar los clientes", ex);
            ventana.muestraDialogoConMensaje("Error al recuperar los clientes: " + ex.getMessage());
        }
    }

    /**
     * Filtra los clientes por nombre, teléfono o número de cliente.
     * Si el criterio está vacío, muestra todos.
     *
     * @param criterio texto escrito por el usuario
     */
    public void buscaClientes(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            cargaTodos();
            return;
        }

        try {
            List<Cliente> clientes = servicioCliente.buscaClientes(criterio);
            ventana.muestraClientes(clientes);

            if (clientes.isEmpty()) {
                ventana.muestraDialogoConMensaje("No se encontraron clientes con: " + criterio.trim());
            }
        } catch (Exception ex) {
            log.error("Error al buscar clientes", ex);
            ventana.muestraDialogoConMensaje("Error al buscar clientes: " + ex.getMessage());
        }
    }

    /**
     * Confirma el cliente elegido, avisa a quien abrió el diálogo y lo cierra.
     *
     * @param cliente cliente seleccionado en la tabla
     */
    public void seleccionaCliente(Cliente cliente) {
        if (cliente == null) {
            ventana.muestraDialogoConMensaje("Seleccione un cliente de la lista.");
            return;
        }

        if (alSeleccionar != null) {
            alSeleccionar.accept(cliente);
        }
        ventana.setVisible(false);
    }

    /**
     * Cierra el diálogo sin seleccionar a nadie.
     */
    public void termina() {
        ventana.setVisible(false);
    }
}