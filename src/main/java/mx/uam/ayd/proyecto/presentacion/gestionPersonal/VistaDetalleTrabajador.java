package mx.uam.ayd.proyecto.presentacion.gestionPersonal;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import mx.uam.ayd.proyecto.negocio.modelo.Trabajador;

@Component
public class VistaDetalleTrabajador {

    @Autowired
    private ApplicationContext context;

    private Stage stage;
    private ControlTrabajador control;
    private boolean initialized = false;
    private Trabajador trabajadorActual;

    @FXML
    private Label lblNombre;

    @FXML
    private Label lblRol;

    @FXML
    private Label lblNumeroTrabajador;

    @FXML
    private Label lblTelefono;

    @FXML
    private Label lblCorreo;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnEliminar;

    public void setControl(ControlTrabajador control) {
        this.control = control;
    }

    private void initializeUI() {
        if (initialized) {
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Detalles del Trabajador");
            stage.initModality(Modality.APPLICATION_MODAL);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vista-detalle-trabajador.fxml"));
           loader.setControllerFactory(clazz -> {
                if (clazz == VistaDetalleTrabajador.class) {
                    return this;
                }
                return context.getBean(clazz);
            });

            BorderPane root = loader.load();
            Scene scene = new Scene(root, 450, 400);
            stage.setScene(scene);

            initialized = true;
        } catch (IOException e) {
            System.err.println("Error al cargar la interfaz FXML de vista-detalle-trabajador.fxml:");
            e.printStackTrace();
        }
    }

    /**
     * Mensaje exacto indicado por el Diagrama de Secuencia: muestraDetalle(trabajador)
     */
    public void muestraDetalle(Trabajador trabajador) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraDetalle(trabajador));
            return;
        }

        this.trabajadorActual = trabajador;
        initializeUI();

        if (trabajador != null) {
            if (lblNombre != null) lblNombre.setText(trabajador.getNombreCompleto());
            if (lblRol != null) lblRol.setText(trabajador.getRol());
            if (lblNumeroTrabajador != null) lblNumeroTrabajador.setText(trabajador.getNumeroTrabajador());
            if (lblTelefono != null) lblTelefono.setText(trabajador.getTelefono());
            // Si en tu modelo usas getCorreoElectronico(), cambia getCorreo() por getCorreoElectronico()
            if (lblCorreo != null) lblCorreo.setText(trabajador.getCorreoElectronico());
        }

        stage.show();
    }

    @FXML
    private void handleEditar() {
        if (control != null && trabajadorActual != null) {
            stage.close();
            control.abrirEdicionTrabajador(trabajadorActual);
        }
    }

    @FXML
    private void handleEliminar() {
        if (control != null && trabajadorActual != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText(null);
            alert.setContentText("¿Está seguro de que desea eliminar a " + trabajadorActual.getNombreCompleto() + "?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    stage.close();
                    control.eliminarTrabajador(trabajadorActual.getIdTrabajador());
                }
            });
        }
    }

    @FXML
    private void handleCerrar() {
        if (stage != null) {
            stage.close();
        }
    }
}