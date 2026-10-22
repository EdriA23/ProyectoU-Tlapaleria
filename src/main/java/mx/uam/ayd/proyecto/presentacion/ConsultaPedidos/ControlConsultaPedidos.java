package mx.uam.ayd.proyecto.presentacion.ConsultaPedidos;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.ServicioVenta;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

@Component
public class ControlConsultaPedidos {

    @Autowired
    private ServicioVenta servicioVenta;

    public void inicia() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-consulta-pedidos.fxml"));
            Parent root = loader.load();

            VentanaConsultaPedidos ventana = loader.getController();
            ventana.setControl(this);
            ventana.cargarPedidos(servicioVenta.obtenerPedidosDomicilioActivos());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Consulta y Gestión de Pedidos a Domicilio");
            stage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar la interfaz FXML de consulta de pedidos:");
            e.printStackTrace();
        }
    }

    public void marcarComoEntregado(long idVenta) {
        servicioVenta.cerrarPedido(idVenta);
    }

    public List<Venta> obtenerPedidosActivos() {
        return servicioVenta.obtenerPedidosDomicilioActivos();
    }
}