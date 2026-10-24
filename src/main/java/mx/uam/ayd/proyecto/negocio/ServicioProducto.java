package mx.uam.ayd.proyecto.negocio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Devolucion;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Servicio encargado de la lógica de negocio de los productos.
 * Maneja consultas de stock, entradas de mercancía, actualización de precios y márgenes de ganancia.
 *
 * @author Javitos
 */
@Service
public class ServicioProducto {

    private static final Logger log = LoggerFactory.getLogger(ServicioProducto.class);

    private final ProductoRepository productoRepository;
    private final ServicioMovimientoInventario servicioMovimientoInventario;
    private final ServicioDevolucion servicioDevolucion;

    /**
     * Inyección de dependencias para el repositorio de productos, el servicio de movimientos y el servicio de devolución.
     */
    @Autowired
    public ServicioProducto(
            ProductoRepository productoRepository, 
            ServicioMovimientoInventario servicioMovimientoInventario,
            ServicioDevolucion servicioDevolucion) {
        
        this.productoRepository = productoRepository;
        this.servicioMovimientoInventario = servicioMovimientoInventario;
        this.servicioDevolucion = servicioDevolucion;
    }

    /**
     * Recupera y lista todos los productos registrados en el sistema.
     * 
     * @return Iterable con todos los productos
     */
    public Iterable<Producto> recuperaProductos() {
        log.info("Recuperando todos los productos");
        return productoRepository.findAll();
    }

    /**
     * Busca un producto específico por su nombre ignorando mayúsculas y minúsculas.
     * 
     * @param nombre Nombre del producto a buscar
     * @return El producto encontrado, o null si no existe o el filtro está vacío
     */
    public Producto buscaProducto(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }
        return productoRepository.findByNombreIgnoreCase(nombre.trim());
    }

    /**
     * Verifica si existe suficiente stock de un producto para surtir una cantidad solicitada.
     * 
     * @param producto Producto a evaluar
     * @param cantidad Cantidad de piezas requeridas
     * @return true si hay stock suficiente, false en caso contrario
     */
    public boolean verificaDisponibilidad(Producto producto, int cantidad) {
        log.info("Verificando disponibilidad para el producto: {} (Cantidad solicitada: {})", 
                 producto != null ? producto.getNombre() : "null", cantidad);

        if (producto == null || cantidad <= 0) {
            log.warn("Producto nulo o cantidad inválida");
            return false;
        }

        boolean disponible = producto.getExistenciaActual() >= cantidad;
        
        if (!disponible) {
            log.warn("Stock insuficiente para {}. Disponible: {}, Solicitado: {}", 
                     producto.getNombre(), producto.getExistenciaActual(), cantidad);
        }

        return disponible;
    }

    /**
     * Busca un producto utilizando su clave única de articulo.
     * 
     * @param clave Clave del producto
     * @return El producto encontrado
     */
    public Producto buscaProductoPorClave(String clave) {
        log.info("Buscando producto por clave: {}", clave);
        return productoRepository.findByClave(clave);
    }

    /**
     * Registra una entrada de nueva mercancía sumándola al stock actual del producto
     * y generando el movimiento correspondiente en el historial.
     * 
     * @param clave Clave del producto que recibe la mercancía
     * @param cantidad Número de piezas nuevas que entran al almacén
     * @return true si se registró con éxito, false si el producto no existe
     */
    @Transactional
    public boolean registrarMercancia(String clave, int cantidad) {
        Producto producto = productoRepository.findByClave(clave);

        if (producto == null) {
            return false;
        }

        int existenciaAnterior = producto.getExistenciaActual();
        int nuevaExistencia = existenciaAnterior + cantidad;

        producto.setExistenciaActual(nuevaExistencia);
        Producto productoGuardado = productoRepository.save(producto);

        // Registro automático del movimiento de entrada utilizando el servicio correspondiente
        servicioMovimientoInventario.registrarMovimiento(
            productoGuardado, 
            cantidad, 
            existenciaAnterior, 
            nuevaExistencia, 
            "ENTRADA", 
            "Entrada de mercancía registrada por clave"
        );

        log.info("Se registró entrada de {} unidades para el producto {}", cantidad, clave);

        return true;
    }

    /**
     * Calcula la ganancia monetaria en pesos (Precio Venta - Precio Costo).
     *
     * @param producto producto a evaluar
     * @return ganancia en pesos, o 0.0 si los datos son inválidos
     */
    public Double calculaGananciaEnPesos(Producto producto) {
        if (producto == null) return 0.0;

        Double precioVenta = producto.getPrecio();
        Double precioCosto = producto.getPrecioCompra();

        if (precioVenta == null || precioCosto == null) {
            return 0.0;
        }

        return precioVenta - precioCosto;
    }

    // MÉTODOS PARA LA HISTORIA DE USUARIO (HU-09)

    /**
     * Busca un producto por su identificador numérico único (idProducto).
     * Esencial para localizar el artículo que se va a modificar en la HU-09.
     * 
     * @param idProducto Identificador único del producto
     * @return El producto encontrado
     */
    public Producto buscarProductoPorId(long idProducto) {
        log.info("Buscando producto por idProducto: {}", idProducto);
        return productoRepository.findByIdProducto(idProducto);
    }

    /**
     * Actualiza el precio de un producto y registra el ajuste en el
     * Historial de Movimientos de Inventario (HU-09).
     * 
     * @param idProducto ID del producto cuyo precio va a cambiar
     * @param nuevoPrecio El nuevo valor monetario asignado al producto
     * @return El objeto Producto actualizado y guardado
     */
    @Transactional
    public Producto actualizarPrecioProducto(long idProducto, double nuevoPrecio) {
        log.info("Intentando actualizar precio para el producto con ID: {} a nuevo precio: {}", idProducto, nuevoPrecio);

        if (nuevoPrecio <= 0) {
            log.warn("Intento de actualización con un precio inválido: {}", nuevoPrecio);
            throw new IllegalArgumentException("El precio debe ser un valor mayor a cero.");
        }

        Producto producto = productoRepository.findByIdProducto(idProducto);
        if (producto == null) {
            log.warn("No se encontró el producto con el ID: {}", idProducto);
            throw new IllegalArgumentException("No se encontró ningún producto con el ID: " + idProducto);
        }

        double precioAnterior = producto.getPrecio();
        producto.setPrecio(nuevoPrecio);

        Producto productoGuardado = productoRepository.save(producto);
        log.info("Precio del producto {} (ID: {}) actualizado exitosamente de ${} a ${}", 
                 productoGuardado.getNombre(), idProducto, precioAnterior, nuevoPrecio);

        int existencia = productoGuardado.getExistenciaActual();
        
        servicioMovimientoInventario.registrarMovimiento(
            productoGuardado, 
            0, 
            existencia, 
            existencia, 
            "CAMBIO_PRECIO", 
            "Ajuste de precio: de $" + precioAnterior + " a $" + nuevoPrecio
        );

        log.info("Movimiento de cambio de precio guardado exitosamente para el producto ID: {}", idProducto);

        return productoGuardado;
    }

    /**
     * Método puente para registrar un producto dañado, delegando la responsabilidad
     * a ServicioDevolucion.
     * 
     * @param idProducto Identificador del producto
     * @param cantidad Cantidad dañada
     * @param motivo Motivo de la devolución
     * @return El objeto Devolucion registrado
     */
    @Transactional
    public Devolucion registraProductoDañado(long idProducto, int cantidad, String motivo) {
        log.info("Coordinando registro de producto dañado para ID: {}", idProducto);
        return servicioDevolucion.registrarDevolucionDanado(idProducto, cantidad, motivo);
    }

    // =========================================================================
    // MÉTODOS PARA LA HISTORIA DE USUARIO (HU-28: MARGEN DE GANANCIA)

    /**
     * Recupera los productos para la vista de margen de ganancia.
     */
    public Iterable<Producto> recuperarProductosConMargen() {
        log.info("Recuperando productos para la vista de Margen de Ganancia");
        return productoRepository.findAll();
    }

    /**
     * Calcula el margen en porcentaje.
     */
    public Double calculaMargen(Producto producto) {
        if (producto == null) return 0.0;

        Double precioVenta = producto.getPrecio();
        Double precioCosto = producto.getPrecioCompra();

        if (precioVenta == null || precioCosto == null || precioVenta <= 0) {
            return 0.0;
        }

        return ((precioVenta - precioCosto) / precioVenta) * 100.0;
    }

    /**
     * Valida la regla de negocio RN-01 (Venta > Costo).
     */
    public boolean validarPrecio(Double costo, Double venta) {
        if (venta == null || costo == null || venta <= costo) {
            return false;
        }
        return true;
    }

    /**
     * Actualiza los precios utilizando la validación de la regla RN-01
     * y genera automáticamente un registro en el Historial de Movimientos.
     */
    @Transactional
    public Producto actualizarPrecios(long idProducto, Double nuevoCosto, Double nuevoPrecioVenta) {
        log.info("Actualizando precios para producto ID: {}", idProducto);

        if (!validarPrecio(nuevoCosto, nuevoPrecioVenta)) {
            log.warn("Violación de regla de negocio RN-01 en validación de precios.");
            throw new IllegalArgumentException("El precio de venta debe ser strictly mayor al precio de costo.");
        }

        Producto producto = productoRepository.findByIdProducto(idProducto);
        if (producto == null) {
            throw new IllegalArgumentException("No se encontró ningún producto con el ID: " + idProducto);
        }

        double precioVentaAnterior = producto.getPrecio() != null ? producto.getPrecio() : 0.0;
        double precioCostoAnterior = producto.getPrecioCompra() != null ? producto.getPrecioCompra() : 0.0;

        producto.setPrecioCompra(nuevoCosto);
        producto.setPrecio(nuevoPrecioVenta);

        Producto productoGuardado = productoRepository.save(producto);

        // Registro del movimiento en la bitácora del historial de inventario
        int existencia = productoGuardado.getExistenciaActual();
        servicioMovimientoInventario.registrarMovimiento(
            productoGuardado, 
            0, 
            existencia, 
            existencia, 
            "CAMBIO_PRECIO", 
            String.format("Ajuste de margen: Costo ($%.2f -> $%.2f), Venta ($%.2f -> $%.2f)", 
                precioCostoAnterior, nuevoCosto, precioVentaAnterior, nuevoPrecioVenta)
        );

        log.info("Movimiento de cambio de precio/costo registrado para el producto ID: {}", idProducto);

        return productoGuardado;
    }
}