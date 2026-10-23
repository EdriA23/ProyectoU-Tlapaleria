package mx.uam.ayd.proyecto.presentacion.margenGanancia;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.springframework.stereotype.Component;

import java.io.IOException;

import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Ventana principal para la visualización del margen de ganancia por producto (HU-28).
 */
@Component
public class VentanaMargenDeGanancia {

    private Stage stage;
    private ControlMargenDeGanancia control;
    private ObservableList<Producto> productosData;
    private FilteredList<Producto> filteredData; // Lista filtrada para búsqueda en tiempo real
    
    @FXML
    private TableView<Producto> tablaMargenes;
    
    @FXML
    private TableColumn<Producto, String> colClave;
    
    @FXML
    private TableColumn<Producto, String> colNombre;
    
    @FXML
    private TableColumn<Producto, Double> colCosto;
    
    @FXML
    private TableColumn<Producto, Double> colVenta;

    @FXML
    private TableColumn<Producto, String> colGananciaPesos; // NUEVA COLUMNA PARA GANANCIA EN PESOS

    @FXML
    private TableColumn<Producto, String> colMargen;

    @FXML
    private TableColumn<Producto, Void> colAcciones; // Columna para el botón "Editar Margen"
    
    @FXML
    private TextField txtBusqueda;
    
    private boolean initialized = false;

    /**
     * Constructor donde inicializamos las listas observables
     */
    public VentanaMargenDeGanancia() {
        productosData = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(productosData, p -> true);
    }
    
    /**
     * Prepara y carga los componentes de la interfaz desde el FXML
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
            stage.setTitle("Margen de Ganancia por Producto");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-margen-ganancia.fxml"));
            loader.setControllerFactory(clazz -> this);
            
            Scene scene = new Scene(loader.load(), 950, 550);
            stage.setScene(scene);
            
            // Configuración de columnas
            colClave.setCellValueFactory(new PropertyValueFactory<>("clave"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colCosto.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
            colVenta.setCellValueFactory(new PropertyValueFactory<>("precio"));

            // Cálculo dinámico de la ganancia en pesos ($)
            colGananciaPesos.setCellValueFactory(cellData -> {
                Producto p = cellData.getValue();
                Double ganancia = control.calcularGananciaEnPesos(p);
                return new SimpleStringProperty(String.format("$ %.2f", ganancia));
            });

            // Cálculo dinámico del porcentaje de margen (%)
            colMargen.setCellValueFactory(cellData -> {
                Producto p = cellData.getValue();
                Double margen = control.calcularMargen(p);
                return new SimpleStringProperty(String.format("%.2f %%", margen));
            });

            // Generación dinámica del botón "Editar Margen" en cada fila
            colAcciones.setCellFactory(param -> new TableCell<>() {
                private final Button btnEditar = new Button("Editar Margen");

                {
                    btnEditar.setStyle("-fx-background-color: #eef2f5; -fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-cursor: hand;");
                    btnEditar.setOnAction(event -> {
                        Producto producto = getTableView().getItems().get(getIndex());
                        control.abrirEdicionMargen(producto);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btnEditar);
                    }
                }
            });
            
            tablaMargenes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            // Listener para la barra de búsqueda
            txtBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(producto -> {
                    if (newValue == null || newValue.trim().isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase().trim();
                    if (producto.getClave() != null && producto.getClave().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    if (producto.getNombre() != null && producto.getNombre().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });
            });

            tablaMargenes.setItems(filteredData);
            initialized = true;
            
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de Margen de Ganancia:");
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra la pantalla principal asignándole primero su controlador
     * 
     * @param control instancia del controlador
     */
    public void muestra(ControlMargenDeGanancia control) {
        this.control = control;
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(control));
            return;
        }
        
        initializeUI();
        stage.show();
    }
    
    /**
     * Recibe la lista de productos y los carga en la tabla
     * 
     * @param productos lista de productos del servicio
     */
    public void mostrarProductos(Iterable<Producto> productos) {
        Platform.runLater(() -> {
            productosData.clear();
            if (productos != null) {
                for (Producto p : productos) {
                    productosData.add(p);
                }
            }
        });
    }

    /**
     * Despliega un diálogo de alerta genérico con un mensaje en pantalla
     * 
     * @param mensaje texto a mostrar
     */
    public void muestraDialogoConMensaje(String mensaje) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraDialogoConMensaje(mensaje));
            return;
        }
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Cierra la ventana principal
     */
    @FXML
    private void handleCerrar() {
        stage.close();
    }
}