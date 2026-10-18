package mx.uam.ayd.proyecto.presentacion.registrarPedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.ServicioVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.DescripcionVenta;
import mx.uam.ayd.proyecto.negocio.modelo.ServicioCliente;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

import java.io.IOException;
import java.util.List;

/**
 * Este es el controlador para la HU-17 y sus funcionalidades
 */
@Component
public class ControlRegistrarPedido {

    @Autowired
    private ServicioVenta servicioVenta;

    @Autowired
    private ServicioCliente servicioCliente;

    public void inicia() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/uam/ayd/proyecto/presentacion/pedidoDomicilio/VistaRegistrarPedido.fxml"));

            Parent root = loader.load();
            
    
            ControlRegistrarPedido controlador = loader.getController();
            controlador.setControl(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Pedido a Domicilio");
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setControl(ControlRegistrarPedido controlRegistrarPedido) {

    }

    /**
     * aqui se validan los datos del cliente
     */
    public boolean validarCliente(String nombre, String telefono, String direccion, String numeroCasa, String entreCalles, String colorCasa) {
        Cliente cliente = new Cliente(nombre, telefono, direccion, numeroCasa, entreCalles, colorCasa);
        return servicioCliente.validarDatosCliente(cliente);
    }

    /**
     * Se registra el pedido a domicilio utilizando el servicio de la venta
     */
    public Venta confirmarPedidoDomicilio(Cliente cliente, List<DescripcionVenta> detalles, double montoRecibido) {
        try {
            return servicioVenta.registrarPedidoDomicilio(cliente, detalles, montoRecibido);
        } catch (Exception e) {
            System.err.println("Error al registrar el pedido a domicilio: " + e.getMessage());
            return null;
        }
    }

    /**
     * y aqui se asigna un repartidor al pediddo que se acaaba de registrar
     */
    public void asignarRepartidorAPedido(long idVenta, String nombreRepartidor) {
        servicioVenta.asignarRepartidor(idVenta, nombreRepartidor);
    }
}