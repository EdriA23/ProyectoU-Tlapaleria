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
     * Inicia el flujo cargando la lista de trabajadores
     * y mostrando la ventana principal.
     */
    public void inicia() {
        List<Trabajador> trabajadores = servicioTrabajador.recuperarTrabajador();
        vistaTrabajador.mostrarVentanaTrabajador(this, trabajadores);
    }

    /**
     * Metodo que busca a los trabajadores
     */
    public void buscarTrabajador() {
        List<Trabajador> trabajadores = servicioTrabajador.recuperarTrabajador();
        vistaTrabajador.muestra(trabajadores);
    }
    /**
     * Inicia el flujo del formulario generando el id del trabajador y mostrando el formulario 
     */
    public void abrirFormulario() {
        String idTrabajador = servicioTrabajador.generarIdTrabajador();
        vistaAgregarTrabajador.muestraFormulario(this, idTrabajador);
    }

    /**
     * Crea al nuevo trabajador
     */
    public void realizarRegistro(String nombre, String telefono, String rol, String correo) {
        try {
            Trabajador trabajador = servicioTrabajador.creaTrabajador(nombre, telefono, rol, correo);
            vistaAgregarTrabajador.mensajeExito(trabajador);
            vistaAgregarTrabajador.cerrar();

            // Actualiza la lista de trabajadores
            buscarTrabajador();
        } catch (Exception e) {
            vistaAgregarTrabajador.muestraError("Error al registrar: " + e.getMessage());
        }
    }
}