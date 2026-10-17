package mx.uam.ayd.proyecto.presentacion.margenGanancia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Controlador de la HU-28 para la visualización y actualización del margen de ganancia.
 */
@Component
public class ControlMargenDeGanancia {

    private final ServicioProducto servicioProducto;
    
    ////////////////////////////////////////////////////////////////////// referencias a las vistas
    private final VentanaMargenDeGanancia ventanaMargen;

    ///////////////////////////////////////////////////////////////////////////// constructor para inyección de dependencias
    /**
     * Constructor donde spring nos inyecta el servicio de productos y la vista.
     * 
     * @param servicioProducto el servicio de negocio
     * @param ventanaMargen vista principal de margen de ganancia
     */
    @Autowired
    public ControlMargenDeGanancia(ServicioProducto servicioProducto, VentanaMargenDeGanancia ventanaMargen) {
        this.servicioProducto = servicioProducto;
        this.ventanaMargen = ventanaMargen;
    }

    ////////////////////////////////////////////////////////////////////////////////////// Métodos del controlador
    /**
     * Arranca todo el flujo de la vista de margen de ganancia al abrir la pantalla.
     */
    public void inicia() {
        this.ventanaMargen.muestra(this);  // Muestra la ventana principal
        this.consultarProductos();         // Carga los productos en la tabla
    }

    ////////////////////////////////////////////////////////////////////////////////////////////// obtiene los productos 
    /**
     * Pide los productos al servicio y los manda a la vista para mostrarlos en la tabla.
     */
    public void consultarProductos() {
        Iterable<Producto> productos = servicioProducto.recuperarProductosConMargen();
        ventanaMargen.mostrarProductos(productos);
    }

    /**
     * Pide al servicio calcular el margen de ganancia de un producto para que la vista lo muestre.
     *
     * @param producto producto del cual se calculará el margen
     * @return margen de ganancia en porcentaje
     */
    public Double calcularMargen(Producto producto) {
        return servicioProducto.calculaMargen(producto);
    }

    /**
     * Envía la petición al servicio para actualizar los precios del producto.
     * Captura las validaciones de negocio (RN-01) y manda los mensajes a la vista.
     *
     * @param idProducto id del producto que seleccionó el usuario
     * @param costo nuevo precio de compra ingresado
     * @param venta nuevo precio de venta ingresado
     */
    public void actualizarPrecios(long idProducto, Double costo, Double venta) {
        try {
            // Llama al servicio que contiene la validación de tu diagrama
            servicioProducto.actualizarPrecios(idProducto, costo, venta);
            
            ventanaMargen.muestraDialogoConMensaje("Precios actualizados correctamente.");
            this.consultarProductos(); // Refresca la tabla para ver los nuevos cálculos
            
        } catch (IllegalArgumentException e) {
            // Si no se cumple la regla RN-01 (Venta > Costo), atrapamos el error y se lo mostramos al usuario
            ventanaMargen.muestraDialogoConMensaje(e.getMessage());
        } catch (Exception e) {
            ventanaMargen.muestraDialogoConMensaje("Error inesperado al intentar actualizar los precios.");
        }
    }
}