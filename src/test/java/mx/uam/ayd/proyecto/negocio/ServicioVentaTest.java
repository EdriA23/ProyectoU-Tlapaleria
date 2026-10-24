package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.datos.InventarioRepository;
import mx.uam.ayd.proyecto.datos.MovimientoInventarioRepository;
import mx.uam.ayd.proyecto.datos.ProductoRepository;
import mx.uam.ayd.proyecto.datos.TrabajadorRepository;
import mx.uam.ayd.proyecto.datos.VentaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.DescripcionVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Inventario;
import mx.uam.ayd.proyecto.negocio.modelo.MovimientoInventario;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

@ExtendWith(MockitoExtension.class)
class ServicioVentaTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @Mock
    private TrabajadorRepository trabajadorRepository; // Para HU-17

    @Mock
    private ServicioCliente servicioCliente; // Para HU-17

    @InjectMocks
    private ServicioVenta servicioVenta;

    @Test
    @DisplayName("Debería iniciar una nueva venta vacía")
    void testIniciarVenta() {
        Venta resultado = servicioVenta.iniciarVenta();
        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Debería agregar un producto a una venta existente")
    void testAgregarProductoVentaExistente() {
        Venta venta = new Venta();
        Producto producto = new Producto();
        producto.setNombre("Martillo");
        producto.setPrecio(150.0);

        Venta resultado = servicioVenta.agregarProducto(producto, 2, venta);
        assertNotNull(resultado);
        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería crear e iniciar una venta si la venta enviada es nula al agregar producto")
    void testAgregarProductoVentaNula() {
        Producto producto = new Producto();
        producto.setNombre("Clavos");
        producto.setPrecio(20.0); 

        Venta resultado = servicioVenta.agregarProducto(producto, 10, null);
        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Debería actualizar una venta exitosamente")
    void testActualizarVentaExitoso() {
        Venta venta = new Venta();
        Venta resultado = servicioVenta.actualizarVenta(venta);
        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Debería regresar nulo al intentar actualizar una venta nula")
    void testActualizarVentaNulaRegresaFalse() {
        Venta resultado = servicioVenta.actualizarVenta(null);
        assertNull(resultado);
    }

    @Test
    @DisplayName("Debería registrar una venta completa, actualizar stock en producto e inventario y guardar movimiento")
    void testRegistrarVentaExitoso() {
        Producto producto = new Producto();
        producto.setIdProducto(1L);
        producto.setNombre("Desarmador");
        producto.setExistenciaActual(20);
        producto.setPrecio(50.0);

        DescripcionVenta detalle = new DescripcionVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(5);

        List<DescripcionVenta> detalles = new ArrayList<>();
        detalles.add(detalle);

        Inventario inventario = new Inventario();
        inventario.setIdProducto(1L);
        inventario.setExistenciaActual(20);

        // CORRECCIÓN: Simulamos correctamente la búsqueda en inventario para que el stock disminuya bien
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        double pago = 300.0;
        Venta ventaResultante = servicioVenta.registrarVenta(detalles, pago);

        assertNotNull(ventaResultante);
        assertEquals(15, producto.getExistenciaActual());
        assertEquals(15, inventario.getExistenciaActual());

        verify(productoRepository).save(producto);
        verify(inventarioRepository).save(inventario);
        verify(movimientoRepository).save(any(MovimientoInventario.class));
        verify(ventaRepository).save(any(Venta.class));
    }

    @Test
    @DisplayName("Debería registrar una venta correctamente cuando la lista de detalles es nula")
    void testRegistrarVentaDetallesNulos() {
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venta ventaResultante = servicioVenta.registrarVenta(null, 100.0);

        assertNotNull(ventaResultante);
        assertEquals(100.0, ventaResultante.getCambio());
        verify(productoRepository, never()).save(any());
        verify(inventarioRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    // ==========================================
    // PRUEBAS PARA LA HU-17 (PEDIDOS A DOMICILIO)
    // ==========================================

    @Test
    @DisplayName("Debería registrar un pedido a domicilio exitosamente (HU-17)")
    void testRegistrarPedidoDomicilioExitoso() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan Pérez");
        cliente.setTelefono("5555555555");
        cliente.setDireccion("Calle Principal 123");

        Producto producto = new Producto();
        producto.setIdProducto(1L);
        producto.setExistenciaActual(10);
        producto.setPrecio(100.0);
        
        DescripcionVenta detalle = new DescripcionVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(1);
        List<DescripcionVenta> detalles = List.of(detalle);

        Inventario inventario = new Inventario();
        inventario.setIdProducto(1L);
        inventario.setExistenciaActual(10);

        when(servicioCliente.validarDatosCliente(cliente)).thenReturn(true);
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venta ventaDomicilio = servicioVenta.registrarPedidoDomicilio(cliente, detalles, 200.0, 0);

        assertNotNull(ventaDomicilio);
        assertTrue(ventaDomicilio.isEsDomicilio());
        assertEquals("Registrado", ventaDomicilio.getEstadoEntrega());
        assertEquals(150.0, ventaDomicilio.getTotal(), 0.01); // 100 producto + 50 envío
    }

    @Test
    @DisplayName("Debería lanzar excepción si faltan datos del cliente en domicilio (HU-17)")
    void testRegistrarPedidoDomicilioDatosIncompletos() {
        Cliente cliente = new Cliente();
        when(servicioCliente.validarDatosCliente(cliente)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            servicioVenta.registrarPedidoDomicilio(cliente, new ArrayList<>(), 200.0, 0);
        });
    }
}