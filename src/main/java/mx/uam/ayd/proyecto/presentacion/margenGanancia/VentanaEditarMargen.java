package mx.uam.ayd.proyecto.presentacion.margenGanancia;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.springframework.stereotype.Component;

import java.io.IOException;

import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Ventana modal (emergente) para calcular y editar el margen de ganancia de un producto.
 */
@Component
public class VentanaEditarMargen {

    private Stage stage;
    private ControlMargenDeGanancia control;
    private Producto productoActual;

    @FXML
    private TextField txtProducto;

    @FXML
    private TextField txtCosto;

    @FXML
    private TextField txtVenta;

    @FXML
    private TextField txtMargen;

    private boolean initialized = false;

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
            stage.setTitle("Calcular Margen de Ganancia");
            stage.initModality(Modality.APPLICATION_MODAL); // Hace que la ventana sea modal (emergente)

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-editar-margen.fxml"));
            loader.setControllerFactory(clazz -> this);

            Scene scene = new Scene(loader.load(), 420, 420);
            stage.setScene(scene);

            // Escuchadores para recalcular el porcentaje de margen en tiempo real mientras el usuario escribe
            txtCosto.textProperty().addListener((obs, oldVal, newVal) -> calcularMargenEnTiempoReal());
            txtVenta.textProperty().addListener((obs, oldVal, newVal) -> calcularMargenEnTiempoReal());

            initialized = true;

        } catch (IOException e) {
            System.err.println("Error al cargar la ventana modal de editar margen:");
            e.printStackTrace();
        }
    }

    /**
     * Muestra la ventana emergente cargando los datos del producto seleccionado.
     *
     * @param control instancia del controlador
     * @param producto producto a editar
     */
    public void muestra(ControlMargenDeGanancia control, Producto producto) {
        this.control = control;
        this.productoActual = producto;

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> this.muestra(control, producto));
            return;
        }

        initializeUI();

        // Poblamos los campos con la información del producto
        txtProducto.setText(producto.getNombre() + " (Clave: " + producto.getClave() + ")");
        txtCosto.setText(producto.getPrecioCompra() != null ? producto.getPrecioCompra().toString() : "0.0");
        txtVenta.setText(producto.getPrecio() != null ? producto.getPrecio().toString() : "0.0");

        calcularMargenEnTiempoReal();

        stage.showAndWait();
    }

    /**
     * Re-calcula de forma visual el margen (%) mientras el usuario modifica las cajas de texto.
     */
    private void calcularMargenEnTiempoReal() {
        try {
            double costo = Double.parseDouble(txtCosto.getText().trim());
            double venta = Double.parseDouble(txtVenta.getText().trim());

            if (venta > 0) {
                double margen = ((venta - costo) / venta) * 100.0;
                txtMargen.setText(String.format("%.2f %%", margen));
            } else {
                txtMargen.setText("0.00 %");
            }
        } catch (NumberFormatException e) {
            txtMargen.setText("0.00 %");
        }
    }

    /**
     * Evento al hacer clic en el botón "Guardar Cambios".
     */
    @FXML
    private void handleGuardar() {
        try {
            Double costo = Double.parseDouble(txtCosto.getText().trim());
            Double venta = Double.parseDouble(txtVenta.getText().trim());

            // Llama al controlador para intentar actualizar los precios (valida RN-01)
            boolean exito = control.actualizarPrecios(productoActual.getIdProducto(), costo, venta);

            if (exito) {
                stage.close(); // Cierra el modal si se guardó correctamente
            }
        } catch (NumberFormatException e) {
            control.abrirEdicionMargen(productoActual);
        }
    }

    /**
     * Evento al hacer clic en el botón "Cancelar".
     */
    @FXML
    private void handleCancelar() {
        stage.close();
    }
}