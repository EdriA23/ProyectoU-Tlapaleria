package mx.uam.ayd.proyecto.presentacion.agregarProductos;

import java.io.IOException;

import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.DescripcionVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

@Component
public class VistaAgregarProductos {

    private Stage stage;
    private ControlAgregarProductos control;
    private boolean initialized = false;

    // -------------------------------------------------------------------------
    // CONTROLES FXML: SECCIÓN BÚSQUEDA Y CATÁLOGO DE PRODUCTOS
    // -------------------------------------------------------------------------
    @FXML
    private TextField txtBuscarProducto;

    @FXML
    private TextField txtCantidad;

    @FXML
    private TableView<Producto> tablaCatalogo;

    @FXML
    private TableColumn<Producto, String> colCatNombre;

    @FXML
    private TableColumn<Producto, Double> colCatPrecio;

    @FXML
    private TableColumn<Producto, Integer> colCatStock;

    // Listas observables para filtrado dinámico
    private ObservableList<Producto> listaProductosObservable = FXCollections.observableArrayList();
    private FilteredList<Producto> productosFiltrados;

    // -------------------------------------------------------------------------
    // CONTROLES FXML: SECCIÓN CARRITO Y DETALLE DE LA VENTA
    // -------------------------------------------------------------------------
    @FXML
    private TableView<DescripcionVenta> tablaCarrito;

    @FXML
    private TableColumn<DescripcionVenta, String> colNombre;

    @FXML
    private TableColumn<DescripcionVenta, Double> colPrecio;

    @FXML
    private TableColumn<DescripcionVenta, Integer> colCantidad;

    @FXML
    private TableColumn<DescripcionVenta, Double> colSubtotal;

    @FXML
    private Label lblTotal;

    // Muestra el cliente seleccionado para la venta (o "Público en general")
    @FXML
    private Label lblCliente;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR E INICIALIZACIÓN DE LA INTERFAZ
    // -------------------------------------------------------------------------
    public VistaAgregarProductos() {
    }

    private void initializeUI() {
        if (initialized) {
            return;
        }

        try {
            stage = new Stage();
            stage.setTitle("Agregar Productos a la Venta");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vista-agregar-productos.fxml"));
            loader.setController(this);
            
            BorderPane root = loader.load();
            Scene scene = new Scene(root, 750, 600);
            stage.setScene(scene);

            // 1. Mapeo de columnas de la Tabla del Catálogo
            colCatNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colCatPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
            colCatStock.setCellValueFactory(new PropertyValueFactory<>("existencias")); // Ajusta a tu atributo en Producto (ej: existencias/stock)

            // 2. Mapeo de columnas de la Tabla del Carrito
            colNombre.setCellValueFactory(new PropertyValueFactory<>("productoNombre"));
            colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
            colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
            colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

            // 3. Listener para filtrado en tiempo real al escribir en el TextField (Escenario 1)
            txtBuscarProducto.textProperty().addListener((observable, oldValue, newValue) -> {
                if (productosFiltrados != null) {
                    productosFiltrados.setPredicate(producto -> {
                        if (newValue == null || newValue.trim().isEmpty()) {
                            return true; // Si está vacío muestra todos los productos
                        }
                        String lowerCaseFilter = newValue.toLowerCase();
                        return producto.getNombre().toLowerCase().contains(lowerCaseFilter);
                    });
                }
            });

            initialized = true;
        } catch (IOException e) {
            System.err.println("Error al cargar la interfaz FXML:");
            e.printStackTrace();
        }
    }

    public void setControl(ControlAgregarProductos control) {
        this.control = control;
    }

    // -------------------------------------------------------------------------
    // MÉTODOS DE FLUJO (LLAMADOS DESDE EL CONTROL)
    // -------------------------------------------------------------------------

    /**
     * Muestra todos los productos al abrir la ventana y prepara la vista.
     */
    public void mostrarVentanaVenta(ControlAgregarProductos control, Iterable<Producto> productos) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.mostrarVentanaVenta(control, productos));
            return;
        }

        this.setControl(control);
        initializeUI();

        // Cargar catálogo de productos en la tabla superior
        listaProductosObservable.clear();
        if (productos != null) {
            for (Producto p : productos) {
                listaProductosObservable.add(p);
            }
        }

        // Crear la lista filtrada y ligarla a la tabla del catálogo
        productosFiltrados = new FilteredList<>(listaProductosObservable, p -> true);
        if (tablaCatalogo != null) {
            tablaCatalogo.setItems(productosFiltrados);
        }

        // Limpiar campo de texto y carrito
        if (txtBuscarProducto != null) {
            txtBuscarProducto.clear();
        }

        control.iniciarVenta();
        muestraCliente(null); // Cada venta nueva inicia como "Público en general"

        if (tablaCarrito != null) {
            tablaCarrito.getItems().clear();
        }
        if (lblTotal != null) {
            lblTotal.setText("$0.00");
        }

        if (initialized) {
            stage.show();
        }
    }

    public void mostrarVenta(Venta venta) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.mostrarVenta(venta));
            return;
        }

        if (venta != null && venta.getProductos() != null) {
            tablaCarrito.setItems(FXCollections.observableArrayList(venta.getProductos()));
            lblTotal.setText(String.format("$%.2f", venta.getTotal()));
        }
    }

    /**
     * Muestra en pantalla el cliente seleccionado para la venta.
     *
     * @param cliente cliente seleccionado (null = público en general)
     */
    public void muestraCliente(Cliente cliente) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestraCliente(cliente));
            return;
        }

        if (lblCliente != null) {
            lblCliente.setText(cliente == null
                    ? "Cliente: Público en general"
                    : "Cliente: " + cliente.getNombre() + " (No. " + cliente.getIdCliente() + ", Tel. " + cliente.getTelefono() + ")");
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

    // -------------------------------------------------------------------------
    // HANDLERS FXML (EVENTOS DE BOTONES)
    // -------------------------------------------------------------------------

    @FXML
    private void handleBuscarCliente() {
        if (control != null) {
            control.buscarCliente();
        }
    }

    @FXML
    private void handleQuitarCliente() {
        if (control != null) {
            control.quitarCliente();
        }
    }

    @FXML
    private void handleAgregarProducto() {
        // Prioridad 1: Producto seleccionado directamente en la tabla
        Producto productoSeleccionado = tablaCatalogo.getSelectionModel().getSelectedItem();

        // Prioridad 2: Si no seleccionó en la tabla pero escribió en el buscador, toma el primer resultado
        if (productoSeleccionado == null && productosFiltrados != null && !productosFiltrados.isEmpty()) {
            productoSeleccionado = productosFiltrados.get(0);
        }

        if (productoSeleccionado == null) {
            muestraMensajeError("No se encontró ningún producto disponible para agregar.");
            return;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());

            if (cantidad <= 0) {
                muestraMensajeError("La cantidad debe ser mayor a 0.");
                return;
            }

            // Llamamos a tu HU en el controlador
            control.agregarProductos(productoSeleccionado, cantidad);

        } catch (NumberFormatException e) {
            muestraMensajeError("Ingresa un número entero válido en la cantidad.");
        }
    }

    @FXML
    private void handleEliminarProducto() {
        DescripcionVenta seleccion = tablaCarrito.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            muestraMensajeError("Por favor selecciona un producto del carrito para eliminar.");
            return;
        }

        if (control != null) {
            tablaCarrito.getItems().remove(seleccion);

            // Recalculamos el total básico
            double nuevoTotal = 0.0;
            for (DescripcionVenta item : tablaCarrito.getItems()) {
                if (item != null) {
                    nuevoTotal += item.getSubtotal();
                }
            }
            lblTotal.setText(String.format("$%.2f", nuevoTotal));
        }
    }

    @FXML
    private void handleSiguiente() {
        if (tablaCarrito.getItems().isEmpty()) {
            muestraMensajeError("Debes agregar al menos un producto a la compra para continuar.");
            return;
        }

        if (control != null) {
            control.continuarRegistroVenta();
        }
    }

    @FXML
    private void handleNuevaVenta() {
        if (control != null) {
            control.iniciarVenta();
            muestraCliente(null); // La venta nueva vuelve a "Público en general"
            if (txtBuscarProducto != null) txtBuscarProducto.clear();
            if (tablaCarrito != null) tablaCarrito.getItems().clear();
            if (lblTotal != null) lblTotal.setText("$0.00");
        }
    }

    @FXML
    private void handleCerrar() {
        stage.close();
    }

    /**
     * Muestra u oculta la ventana según el valor booleano indicado.
     * @param visible true para mostrar, false para ocultar/cerrar.
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
                stage.hide(); // Oculta la ventana al avanzar a la HU de tu compañero
            }
        }
    }
}