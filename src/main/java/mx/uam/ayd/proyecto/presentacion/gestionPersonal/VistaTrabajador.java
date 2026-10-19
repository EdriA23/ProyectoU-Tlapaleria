package mx.uam.ayd.proyecto.presentacion.gestionPersonal;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import mx.uam.ayd.proyecto.negocio.modelo.Trabajador;

@Component
public class VistaTrabajador {

    private Stage stage;
    private ControlTrabajador control;
    private boolean initialized = false;

    // -------------------------------------------------------------------------
    // CONTROLES FXML (Mapeados con vista-trabajador.fxml)
    // -------------------------------------------------------------------------
    @FXML
    private Label lblTotalTrabajadores;

    @FXML
    private TextField txtBuscar;

    @FXML
    private VBox contenedorTarjetas;

    @FXML
    private Button btnRegistrarTrabajador;

    @FXML
    private Button btnCancelar;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR E INICIALIZACIÓN DE LA INTERFAZ
    // -------------------------------------------------------------------------
    public VistaTrabajador() {
    }

    private void initializeUI() {
        if (initialized) {
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Gestión de Personal - Trabajadores");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vista-trabajador.fxml"));
            loader.setController(this);

            BorderPane root = loader.load();
            Scene scene = new Scene(root, 750, 600);
            stage.setScene(scene);

            initialized = true;
        } catch (IOException e) {
            System.err.println("Error al cargar la interfaz FXML de VistaTrabajador:");
            e.printStackTrace();
        }
    }

    public void setControl(ControlTrabajador control) {
        this.control = control;
    }

    // -------------------------------------------------------------------------
    // MÉTODOS DE FLUJO (LLAMADOS DESDE EL CONTROL)
    // -------------------------------------------------------------------------

    /**
     * Prepara la vista, recibe la lista de trabajadores y abre el Stage.
     */
    public void mostrarVentanaTrabajador(ControlTrabajador control, List<Trabajador> trabajadores) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.mostrarVentanaTrabajador(control, trabajadores));
            return;
        }

        this.setControl(control);
        initializeUI();

        muestra(trabajadores);

        if (initialized) {
            stage.show();
        }
    }

    /**
     * Corresponde al mensaje muestra(trabajadores) en el diagrama de secuencia.
     */
    public void muestra(List<Trabajador> trabajadores) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(trabajadores));
            return;
        }

        if (contenedorTarjetas != null) {
            contenedorTarjetas.getChildren().clear();

            if (trabajadores != null && !trabajadores.isEmpty()) {
                lblTotalTrabajadores.setText(trabajadores.size() + " trabajadores activos");

                for (Trabajador t : trabajadores) {
                    contenedorTarjetas.getChildren().add(crearTarjetaTrabajador(t));
                }
            } else {
                lblTotalTrabajadores.setText("0 trabajadores activos");
            }
        }
    }

    public void muestraMensajeError(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraMensajeError(mensaje));
            return;
        }

        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void setVisible(boolean visible) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.setVisible(visible));
            return;
        }

        if (stage != null) {
            if (visible) {
                stage.show();
            } else {
                stage.hide();
            }
        }
    }

    // -------------------------------------------------------------------------
    // CONSTRUCCIÓN DINÁMICA DE TARJETAS DE TRABAJADOR
    // -------------------------------------------------------------------------
    private HBox crearTarjetaTrabajador(Trabajador t) {
        HBox tarjeta = new HBox(12);
        tarjeta.setAlignment(Pos.CENTER_LEFT);
        tarjeta.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #E5E7EB; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12;");

        String iniciales = obtenerIniciales(t.getNombreCompleto());
        Label lblAvatar = new Label(iniciales);
        lblAvatar.setStyle("-fx-background-color: #6366F1; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-alignment: center; -fx-min-width: 40; -fx-min-height: 40; -fx-max-width: 40; -fx-max-height: 40; -fx-background-radius: 20;");

        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);

        HBox filaNombre = new HBox(8);
        filaNombre.setAlignment(Pos.CENTER_LEFT);

        Label lblNombre = new Label(t.getNombreCompleto() != null ? t.getNombreCompleto() : "Sin Nombre");
        lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #111827;");

        Label lblRol = new Label(t.getRol() != null ? t.getRol() : "Sin Rol");
        lblRol.setStyle("-fx-text-fill: #4F46E5; -fx-font-weight: bold; -fx-font-size: 11;");

        filaNombre.getChildren().addAll(lblNombre, lblRol);

        Label lblId = new Label("ID: " + (t.getNumeroTrabajador() != null ? t.getNumeroTrabajador() : "EMP-" + t.getIdTrabajador()));
        lblId.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12;");

        info.getChildren().addAll(filaNombre, lblId);

        Button btnVerDetalles = new Button("Ver detalles");
        btnVerDetalles.setStyle("-fx-background-color: transparent; -fx-text-fill: #4F46E5; -fx-font-weight: bold;");

        tarjeta.getChildren().addAll(lblAvatar, info, btnVerDetalles);
        return tarjeta;
    }

    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return "N/A";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length >= 2) {
            return ("" + partes[0].charAt(0) + partes[1].charAt(0)).toUpperCase();
        }
        return ("" + partes[0].charAt(0)).toUpperCase();
    }

    // -------------------------------------------------------------------------
    // HANDLERS FXML (EVENTOS DE BOTONES)
    // -------------------------------------------------------------------------
    @FXML
    private void handleBuscar() {
        if (control != null) {
            control.buscarTrabajador();
        }
    }

    @FXML
    private void handleCerrar() {
        if (stage != null) {
            stage.close();
        }
    }
    @FXML
    private void handleRegistrarTrabajador() {
         if (control != null) {
        control.abrirFormulario();
    }
}
}