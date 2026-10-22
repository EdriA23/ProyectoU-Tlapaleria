package mx.uam.ayd.proyecto.presentacion.principal;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.presentacion.ConsultaPedidos.ControlConsultaPedidos;
import mx.uam.ayd.proyecto.presentacion.actualizarPrecioProductos.ControlActualizarPrecio;
import mx.uam.ayd.proyecto.presentacion.agregarUsuario.ControlAgregarUsuario;
import mx.uam.ayd.proyecto.presentacion.consultarInventario.ControlConsultarInventario;
import mx.uam.ayd.proyecto.presentacion.devolucionProducto.ControlDevolucionProducto;
import mx.uam.ayd.proyecto.presentacion.historialMovimientos.ControlHistorialMovimientos;
import mx.uam.ayd.proyecto.presentacion.listarGrupos.ControlListarGrupos;
import mx.uam.ayd.proyecto.presentacion.listarInventario.ControlListarInventario;
import mx.uam.ayd.proyecto.presentacion.listarUsuarios.ControlListarUsuarios;
import mx.uam.ayd.proyecto.presentacion.registrarMercancia.ControlRegistrarMercancia;
import mx.uam.ayd.proyecto.presentacion.alertaStock.ControlRevisarExistencia;
import mx.uam.ayd.proyecto.presentacion.registroVenta.ControlRegistroVenta;
import mx.uam.ayd.proyecto.presentacion.agregarProductos.ControlAgregarProductos;
import mx.uam.ayd.proyecto.presentacion.consultarProveedor.ControlProveedor;
import mx.uam.ayd.proyecto.presentacion.margenGanancia.ControlMargenDeGanancia;
import mx.uam.ayd.proyecto.presentacion.gestionPersonal.ControlTrabajador; // Import HU-1

/**
 * Control principal del sistema
 */
@Component
public class ControlPrincipal {

    // Atributos de controles existentes
    private final ControlAgregarUsuario controlAgregarUsuario;
    private final ControlListarUsuarios controlListarUsuarios;
    private final ControlListarGrupos controlListarGrupos;
    private final ControlListarInventario controlListarInventario;
    private final ControlRegistrarMercancia controlRegistrarMercancia;
    private final ControlActualizarPrecio controlActualizarPrecio;
    private final ControlDevolucionProducto controlDevolucion;
    private final ControlConsultarInventario controlConsultarInventario; // <--- Recuperado como atributo original
    private final ControlHistorialMovimientos controlHistorialMovimientos;
    private final ControlRevisarExistencia controlRevisarExistencia;
    private final ControlAgregarProductos controlAgregarProductos;
    private final ControlProveedor controlProveedor;

    private final ControlMargenDeGanancia controlMargenDeGanancia; 
    private final ControlConsultaPedidos controlConsultaPedidos; // Para HU-17

    private final ControlTrabajador controlTrabajador;

    private final VentanaPrincipal ventana;
    

    @Autowired
    public ControlPrincipal(
            ControlAgregarUsuario controlAgregarUsuario,
            ControlListarUsuarios controlListarUsuarios,
            ControlListarGrupos controlListarGrupos,
            ControlListarInventario controlListarInventario,
            ControlRegistrarMercancia controlRegistrarMercancia,
            ControlActualizarPrecio controlActualizarPrecio,
            ControlDevolucionProducto controlDevolucion,
            ControlConsultarInventario controlConsultarInventario, // <--- Incluido en el constructor original
            ControlHistorialMovimientos controlHistorialMovimientos,
            ControlRevisarExistencia controlRevisarExistencia,
            ControlRegistroVenta controlRegistroVenta,
            ControlAgregarProductos controlAgregarProductos,
            ControlProveedor controlProveedor,

            ControlMargenDeGanancia controlMargenDeGanancia,
            ControlConsultaPedidos controlConsultaPedidos, 

            ControlMargenDeGanancia controlMargenDeGanancia1, // Parámetro del constructor
            ControlTrabajador controlTrabajador,

            VentanaPrincipal ventana) {

        this.controlAgregarUsuario = controlAgregarUsuario;
        this.controlListarUsuarios = controlListarUsuarios;
        this.controlListarGrupos = controlListarGrupos;
        this.controlListarInventario = controlListarInventario;
        this.controlRegistrarMercancia = controlRegistrarMercancia;
        this.controlActualizarPrecio = controlActualizarPrecio;
        this.controlDevolucion = controlDevolucion;
        this.controlConsultarInventario = controlConsultarInventario; // <--- Asignado
        this.controlHistorialMovimientos = controlHistorialMovimientos;
        this.controlRevisarExistencia = controlRevisarExistencia;
        this.controlAgregarProductos = controlAgregarProductos;
        this.controlProveedor = controlProveedor;
        this.controlMargenDeGanancia = controlMargenDeGanancia1;

        this.controlConsultaPedidos = controlConsultaPedidos;
        this.controlTrabajador = controlTrabajador;

        this.ventana = ventana;
    }

    @PostConstruct
    public void init() {
        ventana.setControlPrincipal(this);
    }

    public void inicia() {
        ventana.muestra();
    }

    // --- MÉTODOS DE ACCIÓN ---

    public void agregarUsuario() {
        controlAgregarUsuario.inicia();
    }

    public void listarUsuarios() {
        controlListarUsuarios.inicia();
    }

    public void listarGrupos() {
        controlListarGrupos.inicia();
    }

    public void listarInventario() {
        controlListarInventario.inicia();
    }

    public void registrarMercancia() {
        controlRegistrarMercancia.inicia();
    }

    public void actualizarPrecio() {
        controlActualizarPrecio.inicia();
    }

    public void devolucionProducto() {
        controlDevolucion.inicia();
    }

    public void consultarInventario() {
        if (controlConsultarInventario != null) {
            ControlConsultarInventario.inicia(); // <--- Llamada limpia al objeto inyectado
        }
    }

    public void historialMovimientos() {
        controlHistorialMovimientos.inicia();
    }

    public void revisarAlertasStock() {
        controlRevisarExistencia.inicia();
    }

    public void realizarVenta() {
        controlAgregarProductos.inicia();
    }

    public void consultarProveedor() {
        controlProveedor.buscaProveedores();
    }

    /**
     * Inicia la HU-28: Margen de Ganancia
     */
    public void margenGanancia() {
        controlMargenDeGanancia.inicia();
    }


    /**
     * Inicia la HU-17: Consulta y Gestión de Pedidos a Domicilio
     */
    public void consultarPedidosDomicilio() {
        if (controlConsultaPedidos != null) {
            controlConsultaPedidos.inicia(); // <--- Abre la ventana correcta de pedidos a domicilio
        }
    }

    public void consultarTrabajador() {
        controlTrabajador.iniciaBusqueda();

    }
}