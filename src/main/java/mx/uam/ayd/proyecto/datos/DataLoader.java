package mx.uam.ayd.proyecto.datos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.DescripcionVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Devolucion;
import mx.uam.ayd.proyecto.negocio.modelo.Factura;
import mx.uam.ayd.proyecto.negocio.modelo.Grupo;
import mx.uam.ayd.proyecto.negocio.modelo.Inventario;
import mx.uam.ayd.proyecto.negocio.modelo.MovimientoInventario;
import mx.uam.ayd.proyecto.negocio.modelo.Pedido;
import mx.uam.ayd.proyecto.negocio.modelo.Producto;
import mx.uam.ayd.proyecto.negocio.modelo.Proveedor;
import mx.uam.ayd.proyecto.negocio.modelo.Trabajador;
import mx.uam.ayd.proyecto.negocio.modelo.Usuario;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private DevolucionRepository devolucionRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoInventarioRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productoRepository.count() == 0) {
            System.out.println(">>> Poblando base de datos limpia para todas las HU...");

            // 1. Inventario
            Inventario inventario = new Inventario();
            inventario.setExistenciaActual(100);
            inventario.setStockMinimo(10);
            inventario = inventarioRepository.save(inventario);

            // 2. Productos
            Producto p1 = new Producto();
            p1.setClave("PROD-001");
            p1.setNombre("Martillo de Uña 16 oz");
            p1.setMarca("Truper");
            p1.setCategoria("Herramienta Manual");
            p1.setTipoProducto("Herramientas");
            p1.setPrecio(145.00);
            p1.setPrecioCompra(90.00);
            p1.setExistenciaActual(25);
            p1.setInventario(inventario);
            p1 = productoRepository.save(p1);

            Producto p2 = new Producto();
            p2.setClave("PROD-002");
            p2.setNombre("Juego de Desarmadores 6 pzas");
            p2.setMarca("Stanley");
            p2.setCategoria("Herramienta Manual");
            p2.setTipoProducto("Herramientas");
            p2.setPrecio(180.50);
            p2.setPrecioCompra(110.00);
            p2.setExistenciaActual(12);
            p2.setInventario(inventario);
            p2 = productoRepository.save(p2);

            // 3. Trabajador
            Trabajador trabajador = new Trabajador();
            trabajador.setNombreCompleto("Juan Pérez López");
            trabajador.setRol("Vendedor");
            trabajador.setTelefono("5551234567");
            trabajador.setCorreoElectronico("juan.perez@tlapaleria.com");
            trabajador.setSalario(8500.00);
            trabajador.setNumeroTrabajador("EMP-001");
            trabajador = trabajadorRepository.save(trabajador);

           
           
            // 4. Usuario y Grupo (Grupo tiene CascadeType.ALL, así que save(grupo) guarda también al Usuario)
            Usuario usuario = new Usuario();
            usuario.setNombre("Carlos");
            usuario.setApellido("Gómez");
            usuario.setEdad(30);

            Grupo grupo = new Grupo();
            grupo.setNombre("Administradores");
            grupo.addUsuario(usuario);
            grupoRepository.save(grupo);

            // 5. Proveedor y Pedido
            Proveedor proveedor = new Proveedor();
            proveedor.setNombreCompleto("Truper México S.A. de C.V.");
            proveedor.setCorporativo("Corporativo Truper");
            proveedor.setTelefono("8001234567");
            proveedor.setTipoProveedor("Herramientas y Ferretería");

            Pedido pedido = new Pedido();
            pedido.setNumeroPedido("PED-2026-01");
            pedido.setFecha(LocalDate.now().toString());
            pedido.setCantidad(10);
            pedido.setTotal(1450.00);
            pedido.setProducto(p1);
            pedido.setProveedor(proveedor);
            proveedor.getPedidos().add(pedido);

            proveedor = proveedorRepository.save(proveedor);

            // 6. Clientes
            Cliente c1 = new Cliente("Guillermo Amador", "5512345678", "Av. San Rafael", "102", "Entre Calle 1 y Calle 2", "Azul");
            Cliente c2 = new Cliente("María Rodríguez", "5587654321", "Calle Hidalgo", "45", "Entre Benito Juárez y Morelos", "Blanca");
            clienteRepository.guardar(c1);
            clienteRepository.guardar(c2);

            // 7. Factura
            Factura factura = new Factura();
            factura.setNumeroFactura("FAC-2026-001");
            factura.setFechaEmision(LocalDate.now().minusDays(5));
            factura.setFechaVencimiento(LocalDate.now().plusDays(15));
            factura.setProveedor(proveedor);
            factura.setMontoTotal(2500.00);
            factura.setSaldoPendiente(1200.00);
            factura.setEstado("PENDIENTE");
            facturaRepository.save(factura);

            // 8. Movimiento de Inventario
            MovimientoInventario mov = new MovimientoInventario();
            mov.setFecha(LocalDateTime.now());
            mov.setTipoMovimiento("CAMBIO_PRECIO");
            mov.setCantidad(0);
            mov.setExistenciaAnterior(25);
            mov.setExistenciaActual(25);
            mov.setObservacion("Carga inicial de catálogo de productos");
            mov.setProducto(p1);
            movimientoInventarioRepository.save(mov);

            // 9. Devolución
            Devolucion devolucion = new Devolucion();
            devolucion.setProducto(p1);
            devolucion.setCantidad(1);
            devolucion.setMotivo("Mango con fisura de fábrica");
            devolucion.setTipoDevolucion("Merma");
            devolucion.setFecha(LocalDateTime.now());
            devolucion.setTrabajador(trabajador);
            devolucion.setProveedor(proveedor);
            devolucion.setNumeroEmpleado(trabajador.getNumeroTrabajador());
            devolucionRepository.save(devolucion);

            // 10. Venta
            Venta venta = new Venta();
            venta.setDate(LocalDateTime.now());
            venta.setIdTrabajador(trabajador.getIdTrabajador());
            venta.setMontoRecibido(200.00);
            venta.setCambio(55.00);

            DescripcionVenta detalle = new DescripcionVenta(p1, 1);
            venta.agregarDetalle(detalle);
            ventaRepository.save(venta);

            System.out.println(">>> ¡Base de datos poblada exitosamente!");
        }
    }
}