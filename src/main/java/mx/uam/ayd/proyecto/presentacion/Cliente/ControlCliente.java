package mx.uam.ayd.proyecto.presentacion.Cliente;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.ServicioCliente;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;


@Component
public class ControlCliente {

    @Autowired
    private ServicioCliente servicioCliente;

    
    public void inicia() {
        try {
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VentanaCliente.fxml"));
            Parent root = loader.load();

            VentanaCliente ventana = loader.getController();
            ventana.setControl(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de Cliente - Datos de Domicilio");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public boolean validarDatosCliente(String nombre, String telefono, String direccion, String numeroCasa, String entreCalles, String colorCasa) {
        Cliente cliente = new Cliente(nombre, telefono, direccion, numeroCasa, entreCalles, colorCasa);
        return servicioCliente.validarDatosCliente(cliente);
    }

    
    public Cliente guardarCliente(String nombre, String telefono, String direccion, String numeroCasa, String entreCalles, String colorCasa) {
        try {
            Cliente cliente = new Cliente(nombre, telefono, direccion, numeroCasa, entreCalles, colorCasa);
            return servicioCliente.guardarCliente(cliente);
        } catch (Exception e) {
            System.err.println("Error al guardar cliente: " + e.getMessage());
            return null;
        }
    }

    
    public Cliente buscarClientePorTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return null;
        }
        return servicioCliente.buscarClientePorTelefono(telefono);
    }
}