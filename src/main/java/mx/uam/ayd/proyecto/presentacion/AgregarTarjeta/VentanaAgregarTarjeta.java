package mx.uam.ayd.proyecto.presentacion.AgregarTarjeta;

import java.io.IOException;
import java.net.URL;
import java.util.function.UnaryOperator;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Modality;
import javafx.stage.Stage;

import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

@Component
public class VentanaAgregarTarjeta {

    private Stage stage;
    private ControlAgregarTarjeta control;
    private Cliente clienteActual;
    private boolean initialized = false;

    @FXML private TextField txtNombreCliente;
    @FXML private TextField txtNombreTitular;
    @FXML private TextField txtNumeroTarjeta;
    @FXML private TextField txtFechaExp;
    @FXML private PasswordField txtCvv;
    @FXML private Button btnGuardarTodo;

    public void setControl(ControlAgregarTarjeta control) {
        this.control = control;
    }

    /**
     * Se ejecuta automáticamente tras cargar el FXML.
     * Restringe los campos numéricos a sus longitudes máximas correspondientes.
     */
    @FXML
    private void initialize() {
        // Restricción para Número de Tarjeta: Máximo 16 dígitos numéricos
        if (txtNumeroTarjeta != null) {
            UnaryOperator<TextFormatter.Change> filtroTarjeta = change -> {
                String newText = change.getControlNewText();
                if (newText.matches("\\d{0,16}")) {
                    return change;
                }
                return null;
            };
            txtNumeroTarjeta.setTextFormatter(new TextFormatter<>(filtroTarjeta));
        }

        // Restricción para CVV: Máximo 3 dígitos numéricos
        if (txtCvv != null) {
            UnaryOperator<TextFormatter.Change> filtroCvv = change -> {
                String newText = change.getControlNewText();
                if (newText.matches("\\d{0,3}")) {
                    return change;
                }
                return null;
            };
            txtCvv.setTextFormatter(new TextFormatter<>(filtroCvv));
        }
    }

    private void initializeUI() {
        if (initialized) return;

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        URL fxmlUrl = getClass().getResource("/fxml/ventana-agregar-tarjeta-cliente.fxml");
        if (fxmlUrl == null) {
            System.err.println("Error: No se encontró /fxml/ventana-agregar-tarjeta-cliente.fxml");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setControllerFactory(clazz -> this);

            Parent root = loader.load();
            stage = new Stage();
            stage.setTitle("Registrar Nueva Tarjeta");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void muestra(Cliente cliente) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(cliente));
            return;
        }

        this.clienteActual = cliente;
        initializeUI();

        limpiarCampos();

        if (txtNombreCliente != null) {
            if (clienteActual != null) {
                txtNombreCliente.setText(clienteActual.getNombre());
            } else {
                txtNombreCliente.setText("Público General");
            }
        }

        if (stage != null) {
            stage.showAndWait();
        }
    }

    private void limpiarCampos() {
        if (txtNombreTitular != null) txtNombreTitular.clear();
        if (txtNumeroTarjeta != null) txtNumeroTarjeta.clear();
        if (txtFechaExp != null) txtFechaExp.clear();
        if (txtCvv != null) txtCvv.clear();
    }

    @FXML
    private void onGuardarTarjeta() {
        String numero = txtNumeroTarjeta.getText();
        String fechaExp = txtFechaExp.getText();
        String cvv = txtCvv.getText();
        String tipoTarjeta = "CREDITO"; 

        // Validar presencia de datos
        if (numero == null || numero.trim().isEmpty() ||
            fechaExp == null || fechaExp.trim().isEmpty() ||
            cvv == null || cvv.trim().isEmpty()) {
            muestraMensaje("Todos los campos obligatorios deben ser llenados.");
            return;
        }

        // Validar longitud exacta de 16 dígitos para el número de tarjeta
        if (numero.length() != 16) {
            muestraMensaje("El número de tarjeta debe contener exactamente 16 dígitos.");
            return;
        }

        // Validar longitud exacta de 3 dígitos para el CVV
        if (cvv.length() != 3) {
            muestraMensaje("El código CVV debe contener exactamente 3 dígitos.");
            return;
        }

        if (control != null) {
            control.guardarTarjeta(clienteActual, tipoTarjeta, numero, cvv, fechaExp);
        }
    }

    public void oculta() {
        if (stage != null) {
            stage.close();
        }
    }

    public void muestraMensaje(String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Atención");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}