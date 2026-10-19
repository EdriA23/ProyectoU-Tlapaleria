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
}