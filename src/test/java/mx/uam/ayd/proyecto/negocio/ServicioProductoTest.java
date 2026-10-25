package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;

/**
 * Pruebas unitarias para el ServicioProducto (HU-09 y HU-28).
 * Valida la lógica de negocio relacionada con la actualización de precios,
 * el cálculo de margen de ganancia y el cumplimiento de la regla de negocio RN-01.
 */
@ExtendWith(MockitoExtension.class)
class ServicioProductoTest {

    // Simulamos el repositorio de productos
    @Mock
    private ProductoRepository productoRepository;

    // Simulamos el servicio de movimientos para registrar en el historial el cambio de precio
    @Mock
    private ServicioMovimientoInventario servicioMovimientoInventario;

    // Inyectamos los mocks en el servicio de productos que vamos a probar
    @InjectMocks
    private ServicioProducto servicioProducto;

    // =========================================================================
    // PRUEBAS UNITARIAS PARA LA HISTORIA DE USUARIO HU-09
    // =========================================================================

    /**
     * Prueba 1: Valida el flujo exitoso de actualización de precio de un producto.
     * Comprueba que se modifique el precio, se guarde en el repositorio de productos 
     * y se genere su respectivo registro en el historial de movimientos.
     */
    @Test
    void testActualizarPrecioProductoExitoso() {
        long idProducto = 1L;
        double nuevoPrecio = 55.50;

        // Preparamos un producto simulado con un precio anterior
        Producto productoExistente = new Producto();
        productoExistente.setIdProducto(idProducto);
        productoExistente.setNombre("Martillo");
        productoExistente.setPrecio(40.00);

        // Configuramos el comportamiento de los mocks
        when(productoRepository.findByIdProducto(idProducto)).thenReturn(productoExistente);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoExistente);

        // Ejecutamos el método a probar
        Producto resultado = servicioProducto.actualizarPrecioProducto(idProducto, nuevoPrecio);

        // Validaciones: Comprobamos que el producto actualizado no sea nulo y que su precio haya cambiado al nuevo valor
        assertNotNull(resultado, "El producto actualizado no debe ser nulo");
        assertEquals(nuevoPrecio, resultado.getPrecio(), "El precio del producto debió actualizarse al nuevo valor");
        verify(productoRepository, times(1)).save(any(Producto.class));
        verify(servicioMovimientoInventario, times(1)).registrarMovimiento(
            any(Producto.class), anyInt(), anyInt(), anyInt(), anyString(), anyString()
        );
    }

    /**
     * Prueba 2: Valida que el sistema rechace la operación lanzando una excepción 
     * cuando se intenta actualizar el precio de un producto que no existe en la base de datos.
     */
    @Test
    void testActualizarPrecioProductoInexistente() {
        long idInexistente = 99L;
        when(productoRepository.findByIdProducto(idInexistente)).thenReturn(null);

        // Se espera una excepción al no encontrar el producto
        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.actualizarPrecioProducto(idInexistente, 50.0);
        });
    }

    /**
     * Prueba 3: Valida las reglas de negocio sobre los valores monetarios permitidos.
     * Comprueba que se rechacen precios iguales a cero ($0.0$) o negativos ($\le 0$).
     */
    @Test
    void testActualizarPrecioValoresInvalidos() {
        long idProducto = 1L;

        // Caso A: Intentar actualizar con precio cero debe lanzar excepción
        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.actualizarPrecioProducto(idProducto, 0.0);
        });

        // Caso B: Intentar actualizar con precio negativo debe lanzar excepción
        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.actualizarPrecioProducto(idProducto, -5.0);
        });
    }

    // =========================================================================
    // PRUEBAS UNITARIAS PARA LA HISTORIA DE USUARIO HU-28 (MARGEN DE GANANCIA)
    // =========================================================================

    /**
     * Prueba HU-28: Valida la recuperación de productos para la tabla de margen.
     */
    @Test
    void testRecuperarProductosConMargen() {
        Producto p1 = new Producto();
        p1.setNombre("Desarmador");
        Producto p2 = new Producto();
        p2.setNombre("Pinzas");

        when(productoRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        Iterable<Producto> productos = servicioProducto.recuperarProductosConMargen();

        assertNotNull(productos, "La lista recuperada no debe ser nula");
        int contador = 0;
        for (Producto p : productos) {
            contador++;
        }
        assertEquals(2, contador, "Debe retornar los 2 productos mockeados");
        verify(productoRepository, times(1)).findAll();
    }

    /**
     * Prueba HU-28: Valida el cálculo correcto del porcentaje de margen de ganancia.
     * Fórmula: ((PrecioVenta - PrecioCosto) / PrecioVenta) * 100.
     * Ejemplo: Venta = 100, Costo = 60 -> Margen = 40%.
     */
    @Test
    void testCalculaMargenExitoso() {
        Producto producto = new Producto();
        producto.setPrecio(100.0);      // Precio de Venta
        producto.setPrecioCompra(60.0);   // Precio de Costo

        Double margen = servicioProducto.calculaMargen(producto);

        assertEquals(40.0, margen, 0.001, "El margen calculado debe ser exactamente 40.0%");
    }

    /**
     * Prueba HU-28: Valida que el cálculo de margen retorne 0.0 cuando los datos son inválidos o nulos.
     */
    @Test
    void testCalculaMargenCasosInvalidos() {
        // Caso A: Producto nulo
        assertEquals(0.0, servicioProducto.calculaMargen(null), "Debe retornar 0.0 si el producto es null");

        // Caso B: Precios con valores nulos o venta menor/igual a cero
        Producto pInvalido = new Producto();
        pInvalido.setPrecio(0.0);
        pInvalido.setPrecioCompra(10.0);
        assertEquals(0.0, servicioProducto.calculaMargen(pInvalido), "Debe retornar 0.0 si el precio de venta es 0");
    }

    /**
     * Prueba HU-28: Valida el cálculo correcto de la ganancia en pesos ($).
     * Ejemplo: Venta = 180.5, Costo = 110.0 -> Ganancia = $ 70.50.
     */
    @Test
    void testCalculaGananciaEnPesosExitoso() {
        Producto producto = new Producto();
        producto.setPrecio(180.50);     // Precio de Venta
        producto.setPrecioCompra(110.00); // Precio de Costo

        Double ganancia = servicioProducto.calculaGananciaEnPesos(producto);

        assertEquals(70.50, ganancia, 0.001, "La ganancia en pesos debe ser $70.50");
    }

    /**
     * Prueba HU-28: Valida la regla de negocio RN-01 (Precio Venta > Precio Costo).
     */
    @Test
    void testValidarPrecioReglaDeNegocio() {
        // Caso A: Cumple RN-01 (Venta > Costo)
        assertTrue(servicioProducto.validarPrecio(50.0, 80.0), "Debe ser true cuando Venta > Costo");

        // Caso B: Viola RN-01 (Venta <= Costo)
        assertFalse(servicioProducto.validarPrecio(100.0, 80.0), "Debe ser false cuando Costo > Venta");
        assertFalse(servicioProducto.validarPrecio(50.0, 50.0), "Debe ser false cuando Costo == Venta");

        // Caso C: Parámetros nulos
        assertFalse(servicioProducto.validarPrecio(null, 100.0), "Debe ser false si algún precio es null");
        assertFalse(servicioProducto.validarPrecio(50.0, null), "Debe ser false si algún precio es null");
    }

    /**
     * Prueba HU-28: Valida la actualización de costo y precio de venta respetando la RN-01.
     */
    @Test
    void testActualizarPreciosExitoso() {
        long idProducto = 1L;
        Double nuevoCosto = 60.0;
        Double nuevoPrecioVenta = 100.0;

        Producto productoExistente = new Producto();
        productoExistente.setIdProducto(idProducto);

        when(productoRepository.findByIdProducto(idProducto)).thenReturn(productoExistente);
        when(productoRepository.save(any(Producto.class))).thenReturn(productoExistente);

        Producto resultado = servicioProducto.actualizarPrecios(idProducto, nuevoCosto, nuevoPrecioVenta);

        assertNotNull(resultado, "El producto actualizado no debe ser nulo");
        assertEquals(nuevoCosto, resultado.getPrecioCompra(), "El precio de compra/costo debe haberse actualizado");
        assertEquals(nuevoPrecioVenta, resultado.getPrecio(), "El precio de venta debe haberse actualizado");
        verify(productoRepository, times(1)).save(any(Producto.class));
        verify(servicioMovimientoInventario, times(1)).registrarMovimiento(
            any(Producto.class), anyInt(), anyInt(), anyInt(), anyString(), anyString()
        );
    }

    /**
     * Prueba HU-28: Valida que lanzar excepción cuando la actualización viola la RN-01 (Costo >= Venta).
     */
    @Test
    void testActualizarPreciosViolacionRN01() {
        long idProducto = 1L;
        Double costoInvalido = 100.0;
        Double ventaInvalida = 80.0; // Venta < Costo

        // Debe lanzar IllegalArgumentException debido a la validación de la regla de negocio
        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.actualizarPrecios(idProducto, costoInvalido, ventaInvalida);
        });
    }

    /**
     * Prueba HU-28: Valida lanzar excepción al intentar actualizar precios de un producto inexistente.
     */
    @Test
    void testActualizarPreciosProductoInexistente() {
        long idInexistente = 99L;
        when(productoRepository.findByIdProducto(idInexistente)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.actualizarPrecios(idInexistente, 50.0, 100.0);
        });
    }
}