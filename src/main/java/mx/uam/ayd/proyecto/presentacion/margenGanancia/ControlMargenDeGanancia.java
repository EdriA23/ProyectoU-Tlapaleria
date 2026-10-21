package mx.uam.ayd.proyecto.presentacion.margenGanancia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioProducto;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Controlador para la HU-28 (Margen de Ganancia).
 * Coordina la ventana principal con la tabla de productos y el diálogo modal de edición.
 */
@Component
public class ControlMargenDeGanancia {

    private final ServicioProducto servicioProducto;
    
    ////////////////////////////////////////////////////////////////////// referencias a las vistas
    private final VentanaMargenDeGanancia ventanaMargen;
    private final VentanaEditarMargen ventanaEditar;

    ///////////////////////////////////////////////////////////////////////////// constructor
    /**
     * Inyección de dependencias para el servicio de productos y ambas vistas.
     */
    @Autowired
    public ControlMargenDeGanancia(
            ServicioProducto servicioProducto, 
            VentanaMargenDeGanancia ventanaMargen,
            VentanaEditarMargen ventanaEditar) {
        
        this.servicioProducto = servicioProducto;
        this.ventanaMargen = ventanaMargen;
        this.ventanaEditar = ventanaEditar;
    }

    ////////////////////////////////////////////////////////////////////////////////////// Métodos del controlador
    /**
     * Arranca todo el flujo de la vista de margen de ganancia al abrir la pantalla principal.
     */
    public void inicia() {
        this.ventanaMargen.muestra(this);  // Muestra la ventana principal
        this.consultarProductos();         // Carga los productos en la tabla
    }

    /**
     * Consulta los productos al servicio de negocio y los manda a la tabla de la vista principal.
     */
    public void consultarProductos() {
        Iterable<Producto> productos = servicioProducto.recuperarProductosConMargen();
        ventanaMargen.mostrarProductos(productos);
    }

    /**
     * Solicita el cálculo del margen de ganancia en porcentaje para un producto dado.
     *
     * @param producto objeto producto a calcular
     * @return porcentaje de margen de ganancia
     */
    public Double calcularMargen(Producto producto) {
        return servicioProducto.calculaMargen(producto);
    }

    /**
     * Abre la ventana modal para editar el margen/precios del producto seleccionado.
     *
     * @param producto producto seleccionado en la tabla
     */
    public void abrirEdicionMargen(Producto producto) {
        if (producto != null) {
            this.ventanaEditar.muestra(this, producto);
        } else {
            ventanaMargen.muestraDialogoConMensaje("Por favor, seleccione un producto de la lista.");
        }
    }

    /**
     * Guarda la actualización de precios enviando la petición a la capa de negocio.
     * Valida la regla RN-01 (Venta > Costo).
     *
     * @param idProducto id del producto
     * @param costo nuevo precio de costo
     * @param venta nuevo precio de venta
     * @return true si se actualizó con éxito, false si falló la validación
     */
    public boolean actualizarPrecios(long idProducto, Double costo, Double venta) {
        try {
            // Llama al servicio con la regla RN-01
            servicioProducto.actualizarPrecios(idProducto, costo, venta);
            
            ventanaMargen.muestraDialogoConMensaje("Precios y margen actualizados correctamente.");
            this.consultarProductos(); // Refresca la tabla principal
            return true;
            
        } catch (IllegalArgumentException e) {
            // Atrapa excepciones de validación de negocio (RN-01)
            ventanaMargen.muestraDialogoConMensaje(e.getMessage());
            return false;
        } catch (Exception e) {
            ventanaMargen.muestraDialogoConMensaje("Error inesperado al intentar actualizar los precios.");
            return false;
        }
    }
}