package mx.uam.ayd.proyecto.presentacion.consultarInventario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Control para la ventana de consulta de inventario.
 *
 * @author Yael Mora Simón
 */
@Component
public class ControlConsultarInventario {

    private static ServicioProducto servicioProducto = null;
    private static VentanaConsultarInventario ventana = new VentanaConsultarInventario();

    /**
     * Crea el controlador de la consulta de inventario.
     *
     * @param servicioProducto servicio encargado de los productos
     * @param ventana ventana de consulta de inventario
     */
    @Autowired
    public ControlConsultarInventario(
            ServicioProducto servicioProducto,
            VentanaConsultarInventario ventana) {

        ControlConsultarInventario.servicioProducto = servicioProducto;
        ControlConsultarInventario.ventana = ventana;
    }

    /**
     * Asocia este controlador con la ventana al iniciar la aplicación.
     */
    @PostConstruct
    public void init() {
        ventana.setControl(this);
    }

    /**
     * Recupera todos los productos y los muestra en la ventana.
     */
    public static void inicia() {

        try {
            Iterable<Producto> productos =
                    servicioProducto.recuperaProductos();
            ventana.muestra(productos);
        } catch (Exception ex) {
            ventana.muestraDialogoConMensaje(
                    "Error al recuperar los productos: "
                            + ex.getMessage());
        }
    }

    /**
     * Muestra nuevamente todos los productos registrados.
     */
    public void muestraTodos() {
        inicia();
    }

    /**
     * Busca un producto por su nombre y lo muestra en la tabla.
     *
     * @param nombre nombre del producto a buscar
     */
    public void buscaProducto(String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            ventana.muestraDialogoConMensaje(
                    "Escribe el nombre del producto");
            return;
        }

        try {
            Producto producto =
                    servicioProducto.buscaProducto(nombre.trim());

            if (producto == null) {
                ventana.muestraDialogoConMensaje(
                        "No se encontró un producto con el nombre: "
                                + nombre);
                return;
            }

            ventana.muestraProducto(producto);

        } catch (Exception ex) {
            ventana.muestraDialogoConMensaje(
                    "Error al buscar el producto: "
                            + ex.getMessage());
        }
    }

    /**
     * Cierra la ventana de consulta de inventario.
     */
    public void termina() {
        ventana.setVisible(false);
    }
}