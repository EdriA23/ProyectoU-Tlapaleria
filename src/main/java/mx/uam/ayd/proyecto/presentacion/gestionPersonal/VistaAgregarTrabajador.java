package mx.uam.ayd.proyecto.presentacion.gestionPersonal;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import mx.uam.ayd.proyecto.negocio.modelo.Trabajador;

@Component
public class VistaAgregarTrabajador {

    @Autowired
    private ApplicationContext context;

    private Stage stage;
    private ControlTrabajador control;
    private boolean initialized = false;

    // Componentes FXML
    @FXML
    private TextField txtIdTrabajador;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

    @FXML
    private ComboBox<String> cmbRol;

    @FXML
    private TextField txtCorreo;

    private void initializeUI() {
        if (initialized) {
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Registrar Nuevo Trabajador");
            stage.initModality(Modality.APPLICATION_MODAL);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vista-agregar-trabajador.fxml"));
            
            loader.setControllerFactory(clazz -> {
                if (clazz == VistaAgregarTrabajador.class) {
                    return this;
                }
                return context.getBean(clazz);
            });

            BorderPane root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            if (cmbRol != null) {
                cmbRol.setItems(FXCollections.observableArrayList("Encargado", "Vendedor", "Almacenista", "Administrador","Repartidor"));
            }

            initialized = true;
        } catch (IOException e) {
            System.err.println("Error al cargar la interfaz FXML de vista-agregar-trabajador.fxml:");
            e.printStackTrace();
        }
    }

    public void muestraFormulario(ControlTrabajador control, String idTrabajador) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraFormulario(control, idTrabajador));
            return;
        }

        this.control = control;
        initializeUI();

        limpiarCampos();
        if (txtIdTrabajador != null) {
            txtIdTrabajador.setText(idTrabajador);
        }

        stage.showAndWait();
    }

    private boolean validaInformacion() {
        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        String rol = cmbRol.getValue();

        if (nombre.isEmpty()) {
            muestraError("El nombre completo es obligatorio.");
            return false;
        }

        if (rol == null || rol.trim().isEmpty()) {
            muestraError("Debe seleccionar un puesto/rol para el trabajador.");
            return false;
        }

        return true;
    }

    @FXML
    private void handleConfirmar() {
        if (validaInformacion()) {
            // Se obtiene el ID y los demás campos requeridos
            String idTrabajador = txtIdTrabajador != null ? txtIdTrabajador.getText().trim() : "";
            String nombre = txtNombre.getText().trim();
            String telefono = txtTelefono.getText() != null ? txtTelefono.getText().trim() : "";
            String rol = cmbRol.getValue();
            String correo = txtCorreo.getText() != null ? txtCorreo.getText().trim() : "";

            if (control != null) {
                // Pasa los 5 parámetros al controlador tal como lo indica el diagrama
                control.realizarRegistro(idTrabajador, nombre, telefono, rol, correo);
           
                control.recuperarPersonal();
            }
        }
    }

    public void mensajeExito(Trabajador trabajador) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.mensajeExito(trabajador));
            return;
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText("El trabajador " + trabajador.getNombreCompleto() + " fue registrado correctamente con el ID: " + trabajador.getNumeroTrabajador());
        alert.showAndWait();
    }

    public void muestraError(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraError(mensaje));
            return;
        }

        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarCampos() {
        if (txtNombre != null) txtNombre.clear();
        if (txtTelefono != null) txtTelefono.clear();
        if (txtCorreo != null) txtCorreo.clear();
        if (cmbRol != null) cmbRol.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleCancelar() {
        cerrar();
    }

    public void cerrar() {
        if (stage != null) {
            stage.close();
        }
    }
}