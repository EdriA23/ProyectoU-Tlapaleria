package mx.uam.ayd.proyecto.presentacion.registrarPedido;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

@Component
public class VistaRegistrarPedido implements Initializable {

    @Autowired
    private ControlRegistrarPedido control;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtDireccion;

    @FXML
    private TextField txtNumeroCasa;

    @FXML
    private TextField txtEntreCalles;

    @FXML
    private TextField txtColorCasa;

    @FXML
    private ComboBox<String> cmbRepartidores;

    @FXML
    private Button btnConfirmarPedido;

    @FXML
    private Button btnCancelar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    
        inicializarRepartidores();
    }

  
    public void setControl(ControlRegistrarPedido control) {
        this.control = control;
    }

    private void inicializarRepartidores() {
        if (cmbRepartidores != null) {
            cmbRepartidores.getItems().clear();
            cmbRepartidores.getItems().addAll("Carlos Pérez", "Juan López", "Sin asignar por ahora");
        }
    }

    /**
     * boton de confirmar y Guardar Pedido
     */
    @FXML
    private void handleConfirmarPedido(ActionEvent event) {
        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        String telefono = txtTelefono.getText() != null ? txtTelefono.getText().trim() : "";
        String direccion = txtDireccion.getText() != null ? txtDireccion.getText().trim() : "";
        String numeroCasa = txtNumeroCasa.getText() != null ? txtNumeroCasa.getText().trim() : "";
        String entreCalles = txtEntreCalles.getText() != null ? txtEntreCalles.getText().trim() : "";
        String colorCasa = txtColorCasa.getText() != null ? txtColorCasa.getText().trim() : "";
        String repartidor = cmbRepartidores.getValue();

        // aqui se valida el cliente usando el controlador
        boolean datosValidos = control.validarCliente(nombre, telefono, direccion, numeroCasa, entreCalles, colorCasa);

        if (!datosValidos) {
            mostrarAlerta("Datos incompletos", "Por favor llena todos los campos obligatorios del cliente y su dirección.", AlertType.WARNING);
            return;
        }

        // se construye objeto cliente
        Cliente cliente = new Cliente(nombre, telefono, direccion, numeroCasa, entreCalles, colorCasa);

      
        mostrarAlerta("Éxito", "Pedido a domicilio registrado correctamente. Repartidor: " + (repartidor != null ? repartidor : "Pendiente"), AlertType.INFORMATION);
        
        limpiarCampos();
    }

    /**
     * boton de cancelar
     */
    @FXML
    private void handleCancelar(ActionEvent event) {
        limpiarCampos();
    }

    /**
     * se limpia el formulario tras guardar o cancelar el pedido
     */
    public void limpiarCampos() {
        txtNombre.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtNumeroCasa.clear();
        txtEntreCalles.clear();
        txtColorCasa.clear();
        if (cmbRepartidores != null) {
            cmbRepartidores.getSelectionModel().clearSelection();
        }
    }

    /**
     * se muestran alertas en pantalla.
     */
    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}