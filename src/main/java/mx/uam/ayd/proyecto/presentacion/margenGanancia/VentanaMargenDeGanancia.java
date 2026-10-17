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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.springframework.stereotype.Component;

import java.io.IOException;

import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Ventana principal de javafx para la vista de margen de ganancia.
 */
@Component
public class VentanaMargenDeGanancia {

    private Stage stage;
    private ControlMargenDeGanancia control;
    private ObservableList<Producto> productosData;
    private FilteredList<Producto> filteredData; // Lista filtrada para la búsqueda en tiempo real
    
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
    private TableColumn<Producto, String> colMargen; // Mostraremos el porcentaje aquí
    
    @FXML
    private TextField txtBusqueda;

    @FXML
    private TextField txtCosto;

    @FXML
    private TextField txtVenta;
    
    private boolean initialized = false;

    /**
     * Constructor donde inicializamos las listas observables para llenar la tabla
     */
    public VentanaMargenDeGanancia() {
        productosData = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(productosData, p -> true); // Inicializa aceptando todos los elementos
    }
    
    /**
     * Prepara y carga los componentes de la interfaz grafica de javafx desde el fxml
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
            stage.setTitle("Gestión de Margen de Ganancia");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-margen-ganancia.fxml"));
            loader.setControllerFactory(clazz -> this);
            
            Scene scene = new Scene(loader.load(), 700, 500);
            stage.setScene(scene);
            
            // Configuración de columnas
            colClave.setCellValueFactory(new PropertyValueFactory<>("clave"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colCosto.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
            colVenta.setCellValueFactory(new PropertyValueFactory<>("precio"));

            // El margen lo calculamos en tiempo real con el controlador
            colMargen.setCellValueFactory(cellData -> {
                Producto p = cellData.getValue();
                Double margen = control.calcularMargen(p);
                return new SimpleStringProperty(String.format("%.2f %%", margen));
            });
            
            // Ajuste automático del ancho de las columnas
            tablaMargenes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            // Listener de búsqueda en tiempo real
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

            // Listener para cuando el usuario seleccione un producto en la tabla
            tablaMargenes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    txtCosto.setText(newSelection.getPrecioCompra() != null ? newSelection.getPrecioCompra().toString() : "0.0");
                    txtVenta.setText(newSelection.getPrecio() != null ? newSelection.getPrecio().toString() : "0.0");
                }
            });

            tablaMargenes.setItems(filteredData);
            initialized = true;
            
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de Margen de Ganancia:");
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra la pantalla principal asignandole primero su controlador
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
     * Recibe la lista de productos y los manda a la tabla
     * 
     * @param productos iterador de productos recuperados del servicio
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
     * Despliega un dialogo de alerta generico con un mensaje en pantalla
     * 
     * @param mensaje texto a mostrar en la ventana
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
     * Evento al dar clic en Actualizar Precios
     */
    @FXML
    private void handleActualizar() {
        Producto seleccionado = tablaMargenes.getSelectionModel().getSelectedItem();
        
        if (seleccionado == null) {
            muestraDialogoConMensaje("Por favor, seleccione un producto de la tabla.");
            return;
        }

        try {
            Double nuevoCosto = Double.parseDouble(txtCosto.getText());
            Double nuevoVenta = Double.parseDouble(txtVenta.getText());
            control.actualizarPrecios(seleccionado.getIdProducto(), nuevoCosto, nuevoVenta);
        } catch (NumberFormatException e) {
            muestraDialogoConMensaje("Por favor, ingrese valores numéricos válidos para los precios.");
        }
    }
    
    /**
     * Cierra la ventana actual
     */
    @FXML
    private void handleCerrar() {
        stage.close();
    }
}