package mx.uam.ayd.proyecto.presentacion.Cliente;

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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

/**
 * Ventana JavaFX para la captura y validación de datos del cliente (HU-17).
 */
@Component
public class VentanaCliente implements Initializable {

    @Autowired
    private ControlCliente control;

    // --- COMPONENTES FXML ---
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
    private Button btnGuardarCliente;

    @FXML
    private Button btnBuscarCliente;

    @FXML
    private Button btnLimpiar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicialización de componentes si aplica
    }

    public void setControl(ControlCliente control) {
        this.control = control;
    }

    /**
     * Maneja la acción de buscar un cliente por número de teléfono.
     */
    @FXML
    public void handleBuscarCliente(ActionEvent event) {
        String telefono = txtTelefono.getText() != null ? txtTelefono.getText().trim() : "";

        if (telefono.isEmpty()) {
            mostrarAlerta("Campo requerido", "Ingrese un número telefónico para realizar la búsqueda.", AlertType.WARNING);
            return;
        }

        Cliente clienteEncontrado = control.buscarClientePorTelefono(telefono);

        if (clienteEncontrado != null) {
            txtNombre.setText(clienteEncontrado.getNombre());
            txtDireccion.setText(clienteEncontrado.getDireccion());
            txtNumeroCasa.setText(clienteEncontrado.getNumeroCasa());
            txtEntreCalles.setText(clienteEncontrado.getEntreCalles());
            txtColorCasa.setText(clienteEncontrado.getColorCasa());
            mostrarAlerta("Cliente Encontrado", "Datos cargados correctamente.", AlertType.INFORMATION);
        } else {
            mostrarAlerta("No encontrado", "No se encontró ningún cliente registrado con el teléfono ingresado.", AlertType.INFORMATION);
        }
    }

    /**
     * Maneja el registro, la validación del cliente y la generación del pedido a domicilio (HU-17).
     */
    @FXML
    public void handleGuardarCliente(ActionEvent event) {
        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        String telefono = txtTelefono.getText() != null ? txtTelefono.getText().trim() : "";
        String direccion = txtDireccion.getText() != null ? txtDireccion.getText().trim() : "";
        String numeroCasa = txtNumeroCasa.getText() != null ? txtNumeroCasa.getText().trim() : "";
        String entreCalles = txtEntreCalles.getText() != null ? txtEntreCalles.getText().trim() : "";
        String colorCasa = txtColorCasa.getText() != null ? txtColorCasa.getText().trim() : "";

        boolean datosValidos = control.validarDatosCliente(nombre, telefono, direccion, numeroCasa, entreCalles, colorCasa);

        if (!datosValidos) {
            mostrarAlerta("Datos Incompletos", "Por favor completa todos los datos obligatorios del cliente y su domicilio.", AlertType.WARNING);
            return;
        }

        try {
            // Llama al método que guarda el cliente y registra formalmente el pedido a domicilio vinculado a la venta
            control.guardarClienteYGenerarPedido(nombre, telefono, direccion, numeroCasa, entreCalles, colorCasa);

            mostrarAlerta("Éxito", "Cliente guardado y pedido a domicilio registrado correctamente.", AlertType.INFORMATION);

            // Cierra la ventana actual de gestión de cliente tras el éxito
            Stage stage = (Stage) btnGuardarCliente.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un problema al registrar el pedido: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Maneja la limpieza de los campos de texto.
     */
    @FXML
    public void handleLimpiar(ActionEvent event) {
        limpiarCampos();
    }

    public void limpiarCampos() {
        txtNombre.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtNumeroCasa.clear();
        txtEntreCalles.clear();
        txtColorCasa.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void muestraDialogoConMensaje(String string) {
        
        throw new UnsupportedOperationException("Unimplemented method 'muestraDialogoConMensaje'");
    }

    public void cierraVentana() {
        
        throw new UnsupportedOperationException("Unimplemented method 'cierraVentana'");
    }
}