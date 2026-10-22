package mx.uam.ayd.proyecto.presentacion.gestionPersonal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioTrabajador;
import mx.uam.ayd.proyecto.negocio.modelo.Trabajador;

@Component
public class ControlTrabajador {

    @Autowired
    private ServicioTrabajador servicioTrabajador;

    @Autowired
    private VistaTrabajador vistaTrabajador;

    @Autowired
    private VistaAgregarTrabajador vistaAgregarTrabajador;

    /**
     * Inicia el flujo del modulo cargando la lista de trabajadores
     * y mostrando la ventana principal.
     */
    public void iniciaBusqueda() {
        List<Trabajador> trabajadores = servicioTrabajador.recuperarTrabajador();
        vistaTrabajador.mostrarVentanaTrabajador(this, trabajadores);
    }

    /**
     * Alias por si ControlPrincipal u otro componente invoca inicia()
     */
    public void inicia() {
        iniciaBusqueda();
    }

    /**
     * Método exacto según el Diagrama de Secuencia tras confirmar/aceptar el registro.
     */
    public void recuperarPersonal() {
        List<Trabajador> listaTrabajadoresActualizado = servicioTrabajador.recuperarTrabajador();
        vistaTrabajador.muestra(listaTrabajadoresActualizado);
    }

  /**   
     * Alias de compatibilidad por si alguna vista aún invoca buscarTrabajador()
     * o recuperarPersonalActualizado()
    
    public void buscarTrabajador() {
        recuperarPersonal();
    }

    public void recuperarPersonalActualizado() {
        recuperarPersonal();
    }*/

    /**
     * Inicia el flujo del formulario generando el id del trabajador y mostrando el formulario 
     */
    public void abrirFormulario() {
        String idTrabajador = servicioTrabajador.generarIdTrabajador();
        vistaAgregarTrabajador.muestraFormulario(this, idTrabajador);
    }

    /**
     * Crea al nuevo trabajador pasando el id asignado previamente
     */
    public void realizarRegistro(String idTrabajador, String nombre, String telefono, String rol, String correo) {
        try {
            Trabajador trabajador = servicioTrabajador.creaTrabajador(idTrabajador, nombre, telefono, rol, correo);
            vistaAgregarTrabajador.mensajeExito(trabajador);
            vistaAgregarTrabajador.cerrar();

            // Invoca recuperarPersonal() tal como está especificado en el diagrama de secuencia
            recuperarPersonal();
        } catch (Exception e) {
            vistaAgregarTrabajador.muestraError("Error al registrar: " + e.getMessage());
        }
    }
}