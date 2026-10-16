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
 * Maneja consultas de stock, entradas de mercancía y la actualización de precios (HU-09).
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
     * 1. Valida que el nuevo precio sea un valor positivo mayor a cero.
     * 2. Busca el producto en la base de datos por su ID.
     * 3. Cambia el precio anterior por el nuevo precio y guarda los cambios.
     * 4. Genera de forma automática un registro en la bitácora llamando al servicio de movimientos.
     * 
     * @param idProducto ID del producto cuyo precio va a cambiar
     * @param nuevoPrecio El nuevo valor monetario asignado al producto
     * @return El objeto Producto actualizado y guardado
     */
    @Transactional
    public Producto actualizarPrecioProducto(long idProducto, double nuevoPrecio) {
        log.info("Intentando actualizar precio para el producto con ID: {} a nuevo precio: {}", idProducto, nuevoPrecio);

        // Validación 1: El precio no puede ser cero o negativo
        if (nuevoPrecio <= 0) {
            log.warn("Intento de actualización con un precio inválido: {}", nuevoPrecio);
            throw new IllegalArgumentException("El precio debe ser un valor mayor a cero.");
        }

        // Validación 2: Comprueba que el producto exista en la base de datos
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

        // Registro automático en el historial mediante el servicio de movimientos (Coincide con el diagrama)
        int existencia = productoGuardado.getExistenciaActual();
        
        servicioMovimientoInventario.registrarMovimiento(
            productoGuardado, 
            0, // Cero piezas afectadas porque solo cambió el costo/precio
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
     * a ServicioDevolucion tal como lo estructuraste originalmente.
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

    // MÉTODOS PARA LA HISTORIA DE USUARIO (HU-28: MARGEN DE GANANCIA)

    /**
     * Calcula el margen de ganancia en porcentaje (%) para un producto (HU-28).
     * Fórmula: ((Precio Venta - Precio Compra) / Precio Venta) * 100
     * 
     * @param producto Producto al que se le calculará el margen
     * @return Margen en porcentaje, o 0.0 si los datos no son válidos
     */
    public Double calcularMargenGanancia(Producto producto) {
        if (producto == null) {
            return 0.0;
        }

        Double precioVenta = producto.getPrecio();
        Double precioCosto = producto.getPrecioCompra();

        if (precioVenta == null || precioCosto == null || precioVenta <= 0) {
            return 0.0;
        }

        return ((precioVenta - precioCosto) / precioVenta) * 100.0;
    }

    /**
     * Actualiza el precio de costo (precioCompra) y el precio de venta (precio) de un producto.
     * Aplica la Regla de Negocio RN-01: Venta > Costo (HU-28).
     * 
     * @param idProducto ID del producto
     * @param nuevoCosto Nuevo precio de compra/costo
     * @param nuevoPrecioVenta Nuevo precio de venta al público
     * @return El objeto Producto actualizado y guardado
     * @throws IllegalArgumentException si no cumple la regla RN-01 o si el producto no existe
     */
    @Transactional
    public Producto actualizarPreciosMargen(long idProducto, Double nuevoCosto, Double nuevoPrecioVenta) {
        log.info("Intentando actualizar margen para el producto ID: {}. Nuevo Costo: {}, Nuevo Precio Venta: {}", 
                 idProducto, nuevoCosto, nuevoPrecioVenta);

        // Regla de Negocio RN-01: El precio de venta debe ser estrictamente mayor al costo
        if (nuevoPrecioVenta == null || nuevoCosto == null || nuevoPrecioVenta <= nuevoCosto) {
            log.warn("Intento de actualización violando RN-01 (Venta <= Costo)");
            throw new IllegalArgumentException("El precio de venta debe ser mayor al precio de costo.");
        }

        Producto producto = productoRepository.findByIdProducto(idProducto);
        if (producto == null) {
            log.warn("No se encontró el producto con el ID: {}", idProducto);
            throw new IllegalArgumentException("No se encontró ningún producto con el ID: " + idProducto);
        }

        producto.setPrecioCompra(nuevoCosto);
        producto.setPrecio(nuevoPrecioVenta);

        Producto productoGuardado = productoRepository.save(producto);
        log.info("Precios actualizados con éxito para el producto ID: {}", idProducto);

        return productoGuardado;
    }
}