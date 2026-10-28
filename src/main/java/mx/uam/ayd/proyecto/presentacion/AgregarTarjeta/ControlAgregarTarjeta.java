package mx.uam.ayd.proyecto.presentacion.AgregarTarjeta;

import java.util.function.Consumer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mx.uam.ayd.proyecto.negocio.ServicioTarjeta;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Tarjeta;

@Component
public class ControlAgregarTarjeta {

    private final VentanaAgregarTarjeta ventana;
    private final ServicioTarjeta servicioTarjeta;
    private Consumer<Tarjeta> onSuccessCallback;

    @Autowired
    public ControlAgregarTarjeta(VentanaAgregarTarjeta ventana, ServicioTarjeta servicioTarjeta) {
        this.ventana = ventana;
        this.servicioTarjeta = servicioTarjeta;
    }

    @PostConstruct
    public void init() {
        if (ventana != null) {
            ventana.setControl(this);
        }
    }

    /**
     * Inicia el flujo abriendo la ventana emergente recibiendo un Consumer
     */
    public void inicia(Cliente cliente, Consumer<Tarjeta> onSuccessCallback) {
        this.onSuccessCallback = onSuccessCallback;
        if (ventana != null) {
            ventana.muestra(cliente);
        }
    }

    /**
     * Instancia la tarjeta y la procesa según el tipo de cliente.
     */
    public void guardarTarjeta(Cliente cliente, String tipoTarjeta, String numeroTarjeta, String cvv, String fechaVencimiento) {
        try {
            Tarjeta nuevaTarjeta = new Tarjeta(tipoTarjeta, numeroTarjeta, cvv, fechaVencimiento);
            
            if (cliente != null) {
                // Si existe cliente registrado, persistimos en BD
                servicioTarjeta.guardarTarjetaParaCliente(nuevaTarjeta, cliente.getIdCliente());
            }

            ventana.muestraMensaje("La tarjeta ha sido agregada exitosamente.");
            ventana.oculta();

            // Pasamos la tarjeta recién creada a la callback
            if (onSuccessCallback != null) {
                onSuccessCallback.accept(nuevaTarjeta);
            }
        } catch (Exception e) {
            ventana.muestraMensaje("Error al registrar la tarjeta: " + e.getMessage());
        }
    }
}