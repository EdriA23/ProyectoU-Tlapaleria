package mx.uam.ayd.proyecto.presentacion.Cliente;

import java.io.IOException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.ServicioCliente;
import mx.uam.ayd.proyecto.negocio.ServicioVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

@Component
public class ControlCliente {

    @Autowired
    private ServicioCliente servicioCliente;

    @Autowired
    private ServicioVenta servicioVenta; // Para registrar el pedido a domicilio

    @Autowired
    private ApplicationContext applicationContext;

    private Venta ventaActual; // Venta que viene del carrito
    
    // Referencia a la ventana actual para mostrar alertas o cerrarla
    private VentanaCliente ventanaCliente;

    public void inicia() {
        abrirVentana();
    }

    public void iniciaConVenta(Venta venta) {
        this.ventaActual = venta;
        abrirVentana();
    }

    private void abrirVentana() {
        try {
            URL fxmlLocation = ControlCliente.class.getResource("VentanaCliente.fxml");
            if (fxmlLocation == null) {
                fxmlLocation = ControlCliente.class.getResource("/fxml/ventana-cliente.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            ventanaCliente = loader.getController();
            ventanaCliente.setControl(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de Cliente - Datos de Domicilio");
            stage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar la interfaz FXML del cliente:");
            e.printStackTrace();
        }
    }

    /**
     * Valida los datos del cliente utilizando el servicio de negocio.
     */
    public boolean validarDatosCliente(String nombre, String telefono, String direccion, String numeroCasa, String entreCalles, String colorCasa) {
        Cliente cliente = new Cliente(nombre, telefono, direccion, numeroCasa, entreCalles, colorCasa);
        return servicioCliente.validarDatosCliente(cliente);
    }

    /**
     * Guarda el cliente en la base de datos a través del servicio.
     */
    public Cliente guardarCliente(String nombre, String telefono, String direccion, String numeroCasa, String entreCalles, String colorCasa) {
        try {
            Cliente cliente = new Cliente(nombre, telefono, direccion, numeroCasa, entreCalles, colorCasa);
            return servicioCliente.guardarCliente(cliente);
        } catch (Exception e) {
            System.err.println("Error al guardar cliente: " + e.getMessage());
            return null;
        }
    }

    /**
     * Guarda el cliente y genera formalmente el pedido a domicilio vinculado a la venta del carrito.
     */
    public void guardarClienteYGenerarPedido(String nombre, String telefono, String direccion, String numeroCasa, String entreCalles, String colorCasa) {
        try {
            // 1. Guardar o recuperar cliente
            Cliente cliente = new Cliente(nombre, telefono, direccion, numeroCasa, entreCalles, colorCasa);
            Cliente clienteGuardado = servicioCliente.guardarCliente(cliente);

            // 2. Si hay una venta activa, registrar el pedido a domicilio
            if (ventaActual != null && ventaActual.getProductos() != null && !ventaActual.getProductos().isEmpty()) {
                double montoPago = ventaActual.getTotal() != null ? ventaActual.getTotal() : 0.0;
                
                servicioVenta.registrarPedidoDomicilio(
                    clienteGuardado, 
                    ventaActual.getProductos(), 
                    montoPago, 
                    1L // ID del repartidor por defecto
                );
                
                System.out.println("¡Pedido a domicilio registrado exitosamente!");
                
                // Mostrar mensaje de éxito y cerrar ventana de cliente si existe
                if (ventanaCliente != null) {
                    ventanaCliente.muestraDialogoConMensaje("Pedido a domicilio registrado con éxito.");
                    ventanaCliente.cierraVentana(); // Ocultar o cerrar la ventana
                }
            } else {
                System.out.println("Cliente guardado correctamente (sin venta asociada).");
                if (ventanaCliente != null) {
                    ventanaCliente.muestraDialogoConMensaje("Cliente guardado correctamente.");
                }
            }

        } catch (Exception e) {
            // 3. CAPTURA Y MUESTRA LA ALERTA CLARA AL USUARIO (Ej. No hay repartidores disponibles)
            String mensajeError = (e.getMessage() != null) ? e.getMessage() : "Error al generar el pedido a domicilio.";
            System.err.println("Error: " + mensajeError);
            
            if (ventanaCliente != null) {
                ventanaCliente.muestraDialogoConMensaje("Aviso: " + mensajeError);
            }
        }
    }

    /**
     * Busca un cliente por su número telefónico.
     */
    public Cliente buscarClientePorTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return null;
        }
        return servicioCliente.buscarClientePorTelefono(telefono);
    }
}