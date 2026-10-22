package mx.uam.ayd.proyecto.presentacion.ConsultaPedidos;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

@Component
public class VentanaConsultaPedidos implements Initializable {

    private ControlConsultaPedidos control;

    @FXML
    private TableView<Venta> tablaPedidos;
    @FXML
    private TableColumn<Venta, Long> colIdPedido;
    @FXML
    private TableColumn<Venta, String> colRepartidor;
    @FXML
    private TableColumn<Venta, String> colEstado;
    @FXML
    private TableColumn<Venta, Double> colTotal;

    @FXML
    private Button btnMarcarEntregado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colIdPedido.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colRepartidor.setCellValueFactory(new PropertyValueFactory<>("repartidorAsignado"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoEntrega"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    public void setControl(ControlConsultaPedidos control) {
        this.control = control;
    }

    public void cargarPedidos(List<Venta> listaPedidos) {
        ObservableList<Venta> observableList = FXCollections.observableArrayList(listaPedidos);
        tablaPedidos.setItems(observableList);
    }

    @FXML
    public void handleMarcarEntregado(ActionEvent event) {
        Venta seleccionada = tablaPedidos.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Selección requerida", "Por favor seleccione un pedido de la tabla para marcarlo como entregado.", AlertType.WARNING);
            return;
        }

        control.marcarComoEntregado(seleccionada.getIdVenta());
        mostrarAlerta("Éxito", "El pedido ha sido marcado como 'Entregado' y la venta se ha cerrado.", AlertType.INFORMATION);
        
        // Refrescar tabla automáticamente
        cargarPedidos(control.obtenerPedidosActivos());
    }

    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}