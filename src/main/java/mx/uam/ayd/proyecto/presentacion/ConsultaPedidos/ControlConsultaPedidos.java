package mx.uam.ayd.proyecto.presentacion.ConsultaPedidos;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

    @Autowired
    private ApplicationContext applicationContext; // Inyectamos el contexto para usar Spring en el loader

    @Autowired
    private VentanaConsultaPedidos ventanaConsultaPedidos; // Inyectamos directamente la ventana gestionada por Spring

    public void inicia() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ventana-consulta-pedidos.fxml"));
            
            // Forzamos a que FXMLLoader utilice el contenedor de Spring para obtener el controlador
            loader.setControllerFactory(applicationContext::getBean);

            Parent root = loader.load();

            // Como Spring ya inyectó la ventana, solo aseguramos el control y cargamos los datos
            ventanaConsultaPedidos.setControl(this);
            if (servicioVenta != null) {
                ventanaConsultaPedidos.cargarPedidos(servicioVenta.obtenerPedidosDomicilioActivos());
            }

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
        if (servicioVenta != null) {
            servicioVenta.cerrarPedido(idVenta);
        }
    }

    public List<Venta> obtenerPedidosActivos() {
        return servicioVenta != null ? servicioVenta.obtenerPedidosDomicilioActivos() : null;
    }
}