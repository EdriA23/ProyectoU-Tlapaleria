package mx.uam.ayd.proyecto.presentacion.registrarCliente;

import java.io.IOException;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Ventana con el formulario para registrar clientes (HU-12).
 */
@Component
public class VentanaRegistrarCliente {

    private static final String ESTILO_ERROR = "-fx-border-color: red; -fx-border-width: 1.5;";

    private Stage stage;
    private ControlRegistrarCliente control;
    private boolean initialized = false;

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtDireccion;

    public VentanaRegistrarCliente() {
    }

    /**
     * Carga el FXML y prepara la ventana en el hilo de JavaFX.
     */
    private void initializeUI() {
        if (initialized) {
            return;
        }

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Registrar Cliente");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-registrar-cliente.fxml"));
            loader.setController(this);
            stage.setScene(new Scene(loader.load()));

            // Al escribir en un campo marcado en rojo, se quita la marca de error
            txtNombre.textProperty().addListener((obs, anterior, nuevo) -> txtNombre.setStyle(""));
            txtTelefono.textProperty().addListener((obs, anterior, nuevo) -> txtTelefono.setStyle(""));

            initialized = true;
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de registro de clientes:");
            e.printStackTrace();
        }
    }

    public void setControl(ControlRegistrarCliente control) {
        this.control = control;
    }

    /**
     * Muestra la ventana con el formulario vacío.
     */
    public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::muestra);
            return;
        }

        initializeUI();
        limpiaCampos();

        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }

    /**
     * Muestra un diálogo informativo.
     *
     * @param mensaje texto a mostrar
     */
    public void muestraDialogoConMensaje(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraDialogoConMensaje(mensaje));
            return;
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Registro de clientes");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo de error o advertencia.
     *
     * @param mensaje texto a mostrar
     */
    public void muestraError(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraError(mensaje));
            return;
        }

        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Registro de clientes");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra u oculta la ventana.
     *
     * @param visible true para mostrar, false para ocultar
     */
    public void setVisible(boolean visible) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.setVisible(visible));
            return;
        }

        if (!initialized && visible) {
            initializeUI();
        }

        if (stage != null) {
            if (visible) {
                stage.show();
            } else {
                stage.hide();
            }
        }
    }

    private void limpiaCampos() {
        if (txtNombre != null) { txtNombre.clear(); txtNombre.setStyle(""); }
        if (txtTelefono != null) { txtTelefono.clear(); txtTelefono.setStyle(""); }
        if (txtCorreo != null) { txtCorreo.clear(); }
        if (txtDireccion != null) { txtDireccion.clear(); }
    }

    private boolean estaVacio(TextField campo) {
        return campo.getText() == null || campo.getText().trim().isEmpty();
    }

    // --- Manejadores de eventos FXML ---

    @FXML
    private void handleGuardar() {
        boolean nombreVacio = estaVacio(txtNombre);
        boolean telefonoVacio = estaVacio(txtTelefono);

        // Escenario 2: campos obligatorios faltantes se marcan en rojo
        if (nombreVacio || telefonoVacio) {
            if (nombreVacio) {
                txtNombre.setStyle(ESTILO_ERROR);
            }
            if (telefonoVacio) {
                txtTelefono.setStyle(ESTILO_ERROR);
            }
            muestraError("Debe llenar los campos obligatorios: Nombre y Teléfono");
            return;
        }

        if (control == null) {
            return;
        }

        boolean registrado = control.registrarCliente(
                txtNombre.getText(),
                txtTelefono.getText(),
                txtCorreo.getText(),
                txtDireccion.getText());

        if (registrado) {
            limpiaCampos();
        }
    }

    @FXML
    private void handleBuscarClientes() {
        if (control != null) {
            control.buscarClientes();
        }
    }

    @FXML
    private void handleCancelar() {
        if (control != null) {
            control.termina();
        } else {
            setVisible(false);
        }
    }
}