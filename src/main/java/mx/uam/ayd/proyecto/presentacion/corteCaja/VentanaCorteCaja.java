

package mx.uam.ayd.proyecto.presentacion.corteCaja;

import java.io.IOException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Ventana principal para realizar el corte de caja (HU-15).
 *
 * Esta clase carga la interfaz FXML y permite mostrar u ocultar
 * la ventana. Spring proporciona la instancia del controlador.
 */
@Component
public class VentanaCorteCaja {

    private Stage stage;
    private boolean initialized = false;

    private final ApplicationContext applicationContext;

    /**
     * Constructor administrado por Spring.
     *
     * @param applicationContext contexto para obtener el controlador
     */
    @Autowired
    public VentanaCorteCaja(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Inicializa la ventana utilizando el archivo FXML.
     */
    private void initializeUI() {

        if (initialized) {
            return;
        }

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::initializeUI);
            return;
        }

        URL fxmlUrl = getClass().getResource(
                "/fxml/ventana-corte-caja.fxml");

        if (fxmlUrl == null) {
            System.err.println(
                    "No se encontró el FXML "
                    + "/fxml/ventana-corte-caja.fxml");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(fxmlUrl);

            // Obtiene el controlador desde Spring para reutilizar
            // la instancia administrada por el contenedor.
            loader.setControllerFactory(
                    clazz -> applicationContext.getBean(clazz));

            // Carga los componentes definidos en el archivo FXML.
            Parent root = loader.load();

            // Crea la ventana y configura su contenido.
            stage = new Stage();
            stage.setTitle("Corte de caja");
            stage.setScene(new Scene(root));

            // Evita cargar nuevamente la interfaz.
            initialized = true;

        } catch (IOException e) {
            System.err.println(
                    "Error al cargar la ventana de corte de caja.");
            e.printStackTrace();
        }
    }

    /**
     * Muestra la ventana de corte de caja.
     */
    public void muestra() {

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::muestra);
            return;
        }

        initializeUI();

        if (stage != null) {
            stage.show();
        }
    }

    /**
     * Muestra u oculta la ventana.
     *
     * @param visible true para mostrarla; false para ocultarla
     */
    public void setVisible(boolean visible) {

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> setVisible(visible));
            return;
        }

        if (stage != null) {
            if (visible) {
                stage.show();
            } else {
                stage.hide();
            }
        }
    }
}
