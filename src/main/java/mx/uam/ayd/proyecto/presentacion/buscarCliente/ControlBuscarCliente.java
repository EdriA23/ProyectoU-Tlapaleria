package mx.uam.ayd.proyecto.presentacion.buscarCliente;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioCliente;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

/**
 * Controlador de la búsqueda de clientes registrados (HU-12).
 */
@Component
public class ControlBuscarCliente {

    private static final Logger log = LoggerFactory.getLogger(ControlBuscarCliente.class);

    private final ServicioCliente servicioCliente;
    private final VentanaBuscarCliente ventana;

    @Autowired
    public ControlBuscarCliente(ServicioCliente servicioCliente, VentanaBuscarCliente ventana) {
        this.servicioCliente = servicioCliente;
        this.ventana = ventana;
    }

    @PostConstruct
    public void init() {
        ventana.setControl(this);
    }

    /**
     * Abre la ventana y carga todos los clientes registrados.
     */
    public void inicia() {
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
     * Cierra la ventana de búsqueda.
     */
    public void termina() {
        ventana.setVisible(false);
    }
}