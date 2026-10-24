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

    @Autowired
    private VistaDetalleTrabajador vistaDetalleTrabajador;
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

   public void consultarDetalles(Long id) {
        try {
            Trabajador trabajador = servicioTrabajador.obtenerDetalles(id);
            if (trabajador != null) {
                vistaDetalleTrabajador.setControl(this); // <-- LÍNEA CLAVE
                vistaDetalleTrabajador.muestraDetalle(trabajador);
            } else {
                vistaTrabajador.muestraMensajeError("No se encontró el trabajador solicitado.");
            }
        } catch (Exception e) {
            vistaTrabajador.muestraMensajeError("Error al consultar detalles: " + e.getMessage());
        }
    }
     public void abrirEdicionTrabajador(Trabajador trabajador) {
        if (trabajador != null) {
            vistaAgregarTrabajador.muestra(this, trabajador);
        }
    }

    public void realizarActualizacion(Trabajador trabajador) {
        try {
            servicioTrabajador.guardar(trabajador);
            vistaAgregarTrabajador.mensajeExito(trabajador);
        } catch (Exception e) {
            vistaAgregarTrabajador.muestraError("Error al actualizar datos: " + e.getMessage());
        }
    }
    /**
     * Elimina el trabajador y actualiza la lista general en la vista principal.
     */
    public void eliminarTrabajador(Long id) {
        try {
            boolean exito = servicioTrabajador.eliminarTrabajador(id);
            if (exito) {
                // Recupera la lista actualizada y refresca VistaTrabajador
                recuperarPersonal();
            } else {
                vistaTrabajador.muestraMensajeError("No se pudo eliminar el trabajador.");
            }
        } catch (Exception e) {
            vistaTrabajador.muestraMensajeError("Error al eliminar trabajador: " + e.getMessage());
        }
    }


}