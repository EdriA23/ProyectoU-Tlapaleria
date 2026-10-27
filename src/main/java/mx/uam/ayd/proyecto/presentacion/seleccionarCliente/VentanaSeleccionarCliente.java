package mx.uam.ayd.proyecto.presentacion.seleccionarCliente;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

/**
 * Diálogo modal para buscar un cliente registrado y seleccionarlo para la venta.
 */
@Component
public class VentanaSeleccionarCliente {

    private Stage stage;
    private ControlSeleccionarCliente control;
    private boolean initialized = false;

    @FXML private TextField txtBusqueda;
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, Long> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colDescuento;
    @FXML private Label lblResultados;

    public VentanaSeleccionarCliente() {
    }

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
            stage.setTitle("Seleccionar Cliente");
            stage.initModality(Modality.APPLICATION_MODAL);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-seleccionar-cliente.fxml"));
            loader.setController(this);
            stage.setScene(new Scene(loader.load()));

            colId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
            colDescuento.setCellValueFactory(cellData -> {
                Double descuento = cellData.getValue().getDescuentoFidelidad();
                return new SimpleStringProperty(descuento == null ? "-" : String.format("%.0f%%", descuento));
            });

            // Doble clic sobre una fila = seleccionar ese cliente
            tablaClientes.setRowFactory(tv -> {
                TableRow<Cliente> fila = new TableRow<>();
                fila.setOnMouseClicked(evento -> {
                    if (evento.getClickCount() == 2 && !fila.isEmpty() && control != null) {
                        control.seleccionaCliente(fila.getItem());
                    }
                });
                return fila;
            });

            initialized = true;
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de selección de clientes:");
            e.printStackTrace();
        }
    }

    public void setControl(ControlSeleccionarCliente control) {
        this.control = control;
    }

    /**
     * Muestra el diálogo con el buscador vacío.
     */
    public void muestra() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::muestra);
            return;
        }

        initializeUI();

        if (txtBusqueda != null) {
            txtBusqueda.clear();
        }

        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }

    /**
     * Carga los clientes recibidos en la tabla.
     *
     * @param clientes lista de clientes a mostrar
     */
    public void muestraClientes(List<Cliente> clientes) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraClientes(clientes));
            return;
        }

        initializeUI();

        if (tablaClientes == null) {
            return;
        }

        List<Cliente> lista = clientes == null ? List.of() : clientes;
        tablaClientes.setItems(FXCollections.observableArrayList(lista));

        if (lblResultados != null) {
            lblResultados.setText(lista.size() + (lista.size() == 1 ? " cliente" : " clientes"));
        }
    }

    public void muestraDialogoConMensaje(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraDialogoConMensaje(mensaje));
            return;
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Seleccionar cliente");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

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

    // --- Manejadores de eventos FXML ---

    @FXML
    private void handleBuscar() {
        if (control != null) {
            control.buscaClientes(txtBusqueda.getText());
        }
    }

    @FXML
    private void handleMostrarTodos() {
        txtBusqueda.clear();
        if (control != null) {
            control.cargaTodos();
        }
    }

    @FXML
    private void handleSeleccionar() {
        if (control != null) {
            control.seleccionaCliente(tablaClientes.getSelectionModel().getSelectedItem());
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
