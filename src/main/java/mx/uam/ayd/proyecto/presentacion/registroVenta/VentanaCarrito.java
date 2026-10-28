package mx.uam.ayd.proyecto.presentacion.registroVenta;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Tarjeta;
import mx.uam.ayd.proyecto.negocio.modelo.DescripcionVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

/**
 * Ventana de javafx para mostrar el resumen de los productos agregados al carrito de compras.
 * 
 * @author Dydier
 */
@Component
public class VentanaCarrito {

    private Stage stage;
    private ControlRegistroVenta control;
    private boolean initialized = false;
    private Venta ventaActual;

    // Elementos FXML existentes
    @FXML private TableView<DescripcionVenta> tablaCarrito;
    @FXML private TableColumn<DescripcionVenta, String> colNombre;
    @FXML private TableColumn<DescripcionVenta, Double> colPrecio;
    @FXML private TableColumn<DescripcionVenta, Integer> colCantidad;
    @FXML private TableColumn<DescripcionVenta, Double> colSubtotal;
    @FXML private Label lblTotal;

    // Nuevos elementos FXML agregados
    @FXML private Label lblClienteInfo;
    @FXML private ListView<Tarjeta> listTarjetas;
    @FXML private Button btnAgregarTarjeta;

    /**
     * Constructor vacio para instanciar la ventana
     */
    public VentanaCarrito() {
    }

    /**
     * Le asigna la referencia del controlador principal a la vista
     * 
     * @param control instancia del controlador
     */
    public void setControl(ControlRegistroVenta control) {
        this.control = control;
    }

    /**
     * Se ejecuta automáticamente tras cargar el archivo FXML
     */
    @FXML
    private void initialize() {
        if (listTarjetas != null) {
            // Configurar el ListView para mostrar CheckBoxes con Callback explícito
            listTarjetas.setCellFactory(CheckBoxListCell.forListView(new javafx.util.Callback<Tarjeta, javafx.beans.value.ObservableValue<Boolean>>() {
                @Override
                public javafx.beans.value.ObservableValue<Boolean> call(Tarjeta tarjeta) {
                    BooleanProperty selected = new SimpleBooleanProperty();
                    selected.addListener((obs, estabaSeleccionado, estaSeleccionado) -> {
                        if (Boolean.TRUE.equals(estaSeleccionado) && ventaActual != null) {
                            ventaActual.setTarjeta(tarjeta);
                            ventaActual.setTipoPago("TARJETA");
                        }
                    });
                    return selected;
                }
            }));
        }
    }

    /**
     * Prepara e inicializa la interfaz grafica cargando el archivo fxml en el hilo de javafx
     */
    private void initializeUI() {
        if (initialized) return;
        
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        URL fxmlUrl = getClass().getResource("/fxml/ventana-carrito-compras.fxml");
        if (fxmlUrl == null) {
            System.err.println(" Error: No se encontró el FXML '/fxml/ventana-carrito-compras.fxml'");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            
            // Usar setControllerFactory para que utilice la misma instancia de Spring
            loader.setControllerFactory(clazz -> this);

            Parent root = loader.load();
            
            stage = new Stage();
            stage.setTitle("Confirmación de Venta - Resumen");
            stage.setScene(new Scene(root));
            
            // Mapeo seguro de columnas
            if (colNombre != null) colNombre.setCellValueFactory(new PropertyValueFactory<>("productoNombre")); 
            if (colPrecio != null) colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
            if (colCantidad != null) colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
            if (colSubtotal != null) colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

            initialized = true;
        } catch (IOException e) {
            System.err.println("Error al cargar la interfaz FXML de VentanaCarrito:");
            e.printStackTrace();
        }
    }

    /**
     * Despliega la ventana recibiendo el objeto Venta completo.
     * @param venta Objeto Venta que contiene la lista de productos y el total acumulado.
     */
    public void muestra(Venta venta) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(venta));
            return;
        }

        if (venta == null) return;
        this.ventaActual = venta;

        initializeUI();
        
        // Carga la tabla directamente leyendo de la Venta de forma segura
        if (tablaCarrito != null && venta.getProductos() != null) {
            tablaCarrito.setItems(FXCollections.observableArrayList(venta.getProductos()));
        }
        
        if (lblTotal != null) {
            lblTotal.setText(String.format("$%.2f", venta.getTotal()));
        }

        // Cargar datos del Cliente y sus Tarjetas
        actualizarDatosClienteYTarjetas();
        
        if (stage != null) {
            stage.show();
        }
    }

    /**
     * Muestra la información del cliente y actualiza el listado de sus tarjetas
     */
    public void actualizarDatosClienteYTarjetas() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::actualizarDatosClienteYTarjetas);
            return;
        }

        if (ventaActual != null && ventaActual.getCliente() != null) {
            Cliente cliente = ventaActual.getCliente();
            if (lblClienteInfo != null) {
                lblClienteInfo.setText("Cliente: " + cliente.getNombre());
            }

            // Consultar tarjetas a través del Control/Servicio
            if (control != null && listTarjetas != null) {
                List<Tarjeta> tarjetas = control.obtenerTarjetasDelCliente(cliente);
                listTarjetas.setItems(FXCollections.observableArrayList(tarjetas));
            }
        } else {
            if (lblClienteInfo != null) {
                lblClienteInfo.setText("Cliente: Venta al Público General (Sin cliente seleccionado)");
            }
            // Para Público General: mostramos la tarjeta si está asignada a ventaActual
            if (listTarjetas != null) {
                if (ventaActual != null && ventaActual.getTarjeta() != null) {
                    listTarjetas.setItems(FXCollections.observableArrayList(List.of(ventaActual.getTarjeta())));
                    listTarjetas.getSelectionModel().select(ventaActual.getTarjeta());
                } else {
                    listTarjetas.getItems().clear();
                }
            }
        }
    }

    /**
     * Acción para abrir la ventana de registro de nueva tarjeta
     */
    @FXML
    private void onAgregarTarjeta() {
        if (control != null) {
            Cliente clienteActual = (ventaActual != null) ? ventaActual.getCliente() : null;
            control.abrirVentanaAgregarTarjeta(clienteActual, this);
        }
    }

    /**
     * Saca una ventanita de alerta o aviso emergente con un texto en pantalla
     * 
     * @param mensaje texto que se le muestra al usuario
     */
    public void muestraDialogoConMensaje(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraDialogoConMensaje(mensaje));
            return;
        }
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Validación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje != null ? mensaje : "");
        alert.showAndWait();
    }

    /**
     * Muestra u oculta la pantalla segun el parametro boolean que le pases
     * 
     * @param visible true para mostrar y false para ocultar
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
     * Accion del boton para confirmar la venta y pasar al cobro
     */
    @FXML
    private void onConfirmarVenta() {
        if (control != null) {
            control.procesarConfirmacionVenta();
        }
    }

    /**
     * Acción del botón para abrir el formulario de pedido a domicilio (HU-17)
     */
    @FXML
    private void onPedidoADomicilio() {
        if (control != null) {
            control.iniciarPedidoADomicilio();
        }
    }

    /**
     * Accion del boton para cancelar el proceso y cerrar las pantallas
     */
    @FXML
    private void onCancelarVenta() {
        if (control != null) {
            control.termina();
        }
    }
}