
package mx.uam.ayd.proyecto.presentacion.corteCaja;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import mx.uam.ayd.proyecto.negocio.ServicioCorteCaja;

/**
 * Controlador de la HU-15: Corte de caja.
 *
 * Coordina las acciones de la interfaz gráfica con el servicio
 * de negocio, respetando la separación de responsabilidades.
 */
@Component
public class ControlCorteCaja {

    private final ServicioCorteCaja servicioCorteCaja;
    private final VentanaCorteCaja ventanaCorteCaja;

    /**
     * Constructor administrado por Spring.
     *
     * @param servicioCorteCaja servicio que aplica las reglas de negocio
     * @param ventanaCorteCaja ventana de corte de caja
     */
    @Autowired
    public ControlCorteCaja(
            ServicioCorteCaja servicioCorteCaja,
            VentanaCorteCaja ventanaCorteCaja) {

        this.servicioCorteCaja = servicioCorteCaja;
        this.ventanaCorteCaja = ventanaCorteCaja;
    }

    /**
     * Abre la ventana de corte de caja.
     */
    public void iniciar() {
        ventanaCorteCaja.muestra();
    }

    /**
     * Cancela el proceso y oculta la ventana sin guardar el corte.
     */
    public void cancelar() {
        ventanaCorteCaja.setVisible(false);
    }

    /**
     * Confirma el corte de caja.
     *
     * Esta implementación es temporal: todavía no consulta las
     * ventas reales ni registra el corte en la base de datos.
     */
    public void confirmar() {

        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Corte de caja");
        alerta.setHeaderText("Corte pendiente de integración");
        alerta.setContentText(
                "La pantalla está preparada, pero todavía falta "
                + "conectar las ventas reales y los datos del turno. "
                + "No se ha guardado ningún corte.");

        alerta.showAndWait();
    }

    /**
     * Maneja el evento del botón Cancelar definido en el FXML.
     * Delegar en cancelar() evita duplicar la lógica.
     */
    @FXML
    private void onCancelar() {
        cancelar();
    }

    /**
     * Maneja el evento del botón Confirmar corte definido en el FXML.
     * Delegar en confirmar() mantiene centralizada la lógica.
     */
    @FXML
    private void onConfirmar() {
        confirmar();
    }

    /**
     * Permite consultar el servicio desde futuras operaciones
     * del controlador, sin acceder directamente al repositorio.
     *
     * @return servicio de corte de caja
     */
    public ServicioCorteCaja getServicioCorteCaja() {
        return servicioCorteCaja;
    }
}
