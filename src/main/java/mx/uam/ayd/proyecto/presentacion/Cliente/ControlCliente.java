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
    private ServicioVenta servicioVenta; // Necesario para registrar el pedido a domicilio

    @Autowired
    private ApplicationContext applicationContext;

    private Venta ventaActual; // Guardamos la venta que viene del carrito

    public void inicia() {
        abrirVentana(null);
    }

    // Método que recibe la venta desde el carrito de compras
    public void iniciaConVenta(Venta venta) {
        this.ventaActual = venta;
        abrirVentana(venta);
    }

    private void abrirVentana(Venta venta) {
        try {
            URL fxmlLocation = ControlCliente.class.getResource("VentanaCliente.fxml");
            if (fxmlLocation == null) {
                fxmlLocation = ControlCliente.class.getResource("/fxml/ventana-cliente.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            VentanaCliente ventana = loader.getController();
            ventana.setControl(this);

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
     * Guarda el cliente y registra formalmente el pedido a domicilio para que aparezca en la tabla
     */
    public void guardarClienteYGenerarPedido(String nombre, String telefono, String direccion, String numeroCasa, String entreCalles, String colorCasa) {
        try {
            // 1. Crear o guardar el cliente
            Cliente cliente = new Cliente(nombre, telefono, direccion, numeroCasa, entreCalles, colorCasa);
            Cliente clienteGuardado = servicioCliente.guardarCliente(cliente);

            // 2. Si tenemos una venta pendiente del carrito, registramos el pedido a domicilio
            if (ventaActual != null && ventaActual.getProductos() != null && !ventaActual.getProductos().isEmpty()) {
                // Registramos el pedido a domicilio usando el servicio de ventas
                servicioVenta.registrarPedidoDomicilio(
                    clienteGuardado, 
                    ventaActual.getProductos(), 
                    ventaActual.getPago() > 0 ? ventaActual.getPago() : ventaActual.getTotal(), 0
                );
                System.out.println("¡Pedido a domicilio registrado con éxito en la base de datos!");
            } else {
                System.out.println("Cliente guardado, pero no había una venta asociada en el carrito.");
            }

        } catch (Exception e) {
            System.err.println("Error al procesar el pedido a domicilio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean validarDatosCliente(String nombre, String telefono, String direccion, String numeroCasa,
            String entreCalles, String colorCasa) {
        
        throw new UnsupportedOperationException("Unimplemented method 'validarDatosCliente'");
    }

    public Cliente buscarClientePorTelefono(String telefono) {
        
        throw new UnsupportedOperationException("Unimplemented method 'buscarClientePorTelefono'");
    }
}