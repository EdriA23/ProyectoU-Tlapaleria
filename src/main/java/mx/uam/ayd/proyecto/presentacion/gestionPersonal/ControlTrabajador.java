package mx.uam.ayd.proyecto.presentacion.gestionPersonal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioTrabajador;
import mx.uam.ayd.proyecto.negocio.modelo.Trabajador;

/**
 * Controlador para la HU-16 (Gestión de Personal - Trabajadores).
 * Se encarga de coordinar la interfaz gráfica con la lógica de negocio.
 */
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
     * Corresponde al mensaje buscarTrabajador() en el diagrama de secuencia.
     */
    public void buscarTrabajador() {
        List<Trabajador> trabajadores = servicioTrabajador.recuperarTrabajador();
        vistaTrabajador.muestra(trabajadores);
    }
    /**
     * Inicia el flujo de alta solicitando el ID al servicio y abriendo la vista de registro.
     */
    public void abrirFormulario() {
        String idTrabajador = servicioTrabajador.generarIdTrabajador();
        vistaAgregarTrabajador.muestraFormulario(this, idTrabajador);
    }

    /**
     * Procesa la creación del nuevo trabajador.
     */
    public void realizarRegistro(String nombre, String telefono, String rol, String correo) {
        try {
            Trabajador trabajador = servicioTrabajador.creaTrabajador(nombre, telefono, rol, correo);
            vistaAgregarTrabajador.mensajeExito(trabajador);
            vistaAgregarTrabajador.cerrar();

            // Refresca la lista en la pantalla principal
            buscarTrabajador();
        } catch (Exception e) {
            vistaAgregarTrabajador.muestraError("Error al registrar: " + e.getMessage());
        }
    }
}