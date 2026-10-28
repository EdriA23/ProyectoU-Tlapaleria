package mx.uam.ayd.proyecto.presentacion.registroVenta;

import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Ventana emergente modal donde se ingresa el efectivo recibido y se calcula el cambio al cobrar.
 * 
 * @author Dydier
 */
@Component
public class VentanaCobro {

    @Autowired
    @Lazy
    private ControlRegistroVenta control;
    
    private Stage stage;
    private boolean initialized = false;

    // Elementos FXML vinculados
    @FXML private Label lblTotalCobro;
    @FXML private TextField txtPago;
    @FXML private Label lblCambio;

    /**
     * Constructor vacio para que spring administre el componente
     */
    public VentanaCobro() {
    }

    /**
     * Guarda la referencia del controlador principal
     * 
     * @param control instancia del controlador
     */
    public void setControl(ControlRegistroVenta control) {
        this.control = control;
    }

    /**
     * Carga y prepara el archivo fxml de la ventana de cobro en el hilo de javafx
     */
    private void initializeUI() {
        if (initialized) return;
        
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-cobro.fxml"));
            
            //Usar la fábrica para usar la misma instancia inyectada por Spring
            loader.setControllerFactory(clazz -> this);

            Parent root = loader.load();
            
            stage = new Stage();
            stage.setTitle("Proceso de Cobro");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana de fondo mientras cobras

            initialized = true;
        } catch (IOException e) {
            System.err.println("Error al cargar la interfaz FXML de VentanaCobro:");
            e.printStackTrace();
        }
    }

    /**
     * Muestra la ventana y prepara los campos para el cobro
     * 
     * @param total el total de dinero a cobrar de la venta
     */
    public void muestra(double total) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(total));
            return;
        }

        initializeUI();
        
        // Limpieza y preparación de etiquetas
        if (txtPago != null) txtPago.setText("");
        if (lblCambio != null) lblCambio.setText("$0.00");
        if (lblTotalCobro != null) lblTotalCobro.setText(String.format("$%.2f", total));
        
        if (stage != null) {
            stage.show();
        }
    }

    /**
     * Actualiza el cambio en pantalla o muestra un mensaje si falta dinero
     * 
     * @param cambio valor numerico del cambio a entregar
     * @param mensaje texto informativo en caso de error o efectivo insuficiente
     */
    public void actualizaCambio(double cambio, String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.actualizaCambio(cambio, mensaje));
            return;
        }
        if (lblCambio != null) {
            if (mensaje != null) {
                lblCambio.setText(mensaje);
            } else {
                lblCambio.setText(String.format("$%.2f", cambio));
            }
        }
    }

    /**
     * Saca una alerta de informacion con el mensaje especificado
     * 
     * @param mensaje texto a mostrar en el dialogo
     */
    public void muestraDialogoConMensaje(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraDialogoConMensaje(mensaje));
            return;
        }
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Sistema de Ventas");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra u oculta la pantalla de cobro.
     * Incluye verificación de nulo para evitar errores si la ventana no fue inicializada (ej. pagos con tarjeta).
     * 
     * @param visible true para desplegar y false para ocultar
     */
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

    /**
     * Evento al presionar la tecla o boton para calcular el cambio segun el efectivo escrito
     */
    @FXML
    private void onCalcularCambio() {
        try {
            if (txtPago != null && !txtPago.getText().isEmpty()) {
                double efectivo = Double.parseDouble(txtPago.getText());
                // Delega al control para validar y calcular
                if (control != null) {
                    control.calcularCambio(efectivo);
                }
            }
        } catch (NumberFormatException e) {
            actualizaCambio(0, "Monto inválido");
        }
    }

    /**
     * Evento del boton cobrar para mandar el pago y registrar la venta en la bd
     */
    @FXML
    private void onRegistrarVenta() {
        if (txtPago == null || txtPago.getText().isEmpty()) {
            muestraDialogoConMensaje("Ingrese el monto recibido.");
            return;
        }
        try {
            double efectivo = Double.parseDouble(txtPago.getText());
            // El controlador finalizará la compra y actualizará el inventario
            if (control != null) {
                control.finalizarCompra(efectivo);
            }
        } catch (NumberFormatException e) {
            muestraDialogoConMensaje("Ingrese un número válido.");
        }
    }
}