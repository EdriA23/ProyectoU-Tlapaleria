package mx.uam.ayd.proyecto.presentacion.gestionPersonal;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
    private boolean initialized = false;

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

    private void initializeUI() {
        if (initialized) {
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Detalles del Trabajador");
            stage.initModality(Modality.APPLICATION_MODAL);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vista-detalle-trabajador.fxml"));
            loader.setControllerFactory(context::getBean);

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

        initializeUI();

        if (trabajador != null) {
            if (lblNombre != null) lblNombre.setText(trabajador.getNombreCompleto());
            if (lblRol != null) lblRol.setText(trabajador.getRol());
            if (lblNumeroTrabajador != null) lblNumeroTrabajador.setText(trabajador.getNumeroTrabajador());
            if (lblTelefono != null) lblTelefono.setText(trabajador.getTelefono());
            if (lblCorreo != null) lblCorreo.setText(trabajador.getCorreoElectronico());
        }

        stage.show();
    }

    @FXML
    private void handleCerrar() {
        if (stage != null) {
            stage.close();
        }
    }
}