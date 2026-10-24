package mx.uam.ayd.proyecto.presentacion.principal;

import java.io.IOException;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Ventana principal usando JavaFX con FXML
 */
@Component
public class VentanaPrincipal {

    private Stage stage;
    private ControlPrincipal control;
    private boolean initialized = false;

    /**
     * Constructor sin inicialización de UI
     */
    public VentanaPrincipal() {
        // No inicializar componentes de JavaFX en el constructor
    }

    /**
     * Inicializa componentes UI en el hilo de aplicación de JavaFX
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
            stage.setTitle("Mi Aplicación");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-principal.fxml"));
            loader.setControllerFactory(clazz -> this);

            Parent root = loader.load();
            Scene scene = new Scene(root, 1366, 768);
            stage.setScene(scene);

            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   public void setControlPrincipal(ControlPrincipal control) {
    this.control = control;
    System.out.println("DEBUG: ControlPrincipal conectado a VentanaPrincipal");
}

    /**
     * Muestra la ventana principal
     */
    public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::muestra);
            return;
        }

        initializeUI();
        stage.show();
    }

    // ==========================================
    // HANDLERS DE EVENTOS FXML
    // ==========================================

    @FXML
    private void handleAgregarUsuario() {
        if (control != null) {
            control.agregarUsuario();
        }
    }

    @FXML
    private void handleListarUsuarios() {
        if (control != null) {
            control.listarUsuarios();
        }
    }

    @FXML
    private void handleListarGrupos() {
        if (control != null) {
            control.listarGrupos();
        }
    }

    @FXML
    private void handleListarInventario() {
        if (control != null) {
            control.listarInventario();
        }
    }

    @FXML
    private void handleRegistrarMercancia() {
        if (control != null) {
            control.registrarMercancia();
        }
    }

    @FXML
    private void handleActualizarPrecio() {
        if (control != null) {
            control.actualizarPrecio();
        }
    }

    @FXML
    private void handleDevolucionProducto() {
        if (control != null) {
            control.devolucionProducto();
        }
    }

    @FXML
    private void abrirVentanaDevolucionProducto() {
        handleDevolucionProducto();
    }

    @FXML
    private void handleRevisarAlertas() {
        if (control != null) {
            control.revisarAlertasStock();
        }
    }

    @FXML
    private void handleRealizarVenta() {
        if (control != null) {
            control.realizarVenta();
        }
    }

    @FXML
    private void handleConsultarInventario() {
        if (control != null) {
            control.consultarInventario();
        }
    }

    @FXML
    private void handleHistorialMovimientos() {
        if (control != null) {
            control.historialMovimientos();
        }
    }

    /**
     * Acción del botón de "Gestionar Proveedores".
     * Delega la responsabilidad a ControlPrincipal para respetar la arquitectura.
     */
    @FXML
    public void handleConsultarProveedor() {
        if (control != null) {
            control.consultarProveedor();
        }
    }
    
    /**
     * Acción del botón de Corte de caja (HU-15).
     * Delega la operación a ControlPrincipal.
     */
    @FXML
  
private void handleCorteCaja() {
    System.out.println("1. Se pulso el boton Corte de caja");

    if (control != null) {
        System.out.println("2. ControlPrincipal esta conectado");
        control.realizarCorteCaja();
    } else {
        System.err.println("ERROR: ControlPrincipal es null");
    }
}


}