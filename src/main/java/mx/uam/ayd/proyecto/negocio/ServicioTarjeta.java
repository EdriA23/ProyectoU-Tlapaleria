package mx.uam.ayd.proyecto.negocio;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.ClienteRepository;
import mx.uam.ayd.proyecto.datos.TarjetaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Tarjeta;

@Service
public class ServicioTarjeta {

    @Autowired
    private TarjetaRepository tarjetaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Devuelve todas las tarjetas del cliente.
     */
    @Transactional(readOnly = true)
    public List<Tarjeta> obtenerTarjetasPorCliente(Cliente cliente) {
        if (cliente == null) {
            return Collections.emptyList();
        }
        return tarjetaRepository.findByCliente(cliente);
    }

    public boolean validarDatosTarjeta(Tarjeta tarjeta) {
        if (tarjeta == null) return false;

        String tipo = tarjeta.getTipoTarjeta();
        String numero = tarjeta.getNumeroTarjeta();
        String cvv = tarjeta.getCvv();
        String vencimiento = tarjeta.getFechaVencimiento();

        if (tipo == null || tipo.trim().isEmpty()) return false;
        if (numero == null || numero.trim().replaceAll("\\s+", "").length() != 16) return false;
        if (cvv == null || (cvv.trim().length() != 3 && cvv.trim().length() != 4)) return false;
        if (vencimiento == null || !vencimiento.trim().matches("^(0[1-9]|1[0-2])\\/([0-9]{2})$")) return false;

        return true;
    }

    /**
     * Guarda la tarjeta asignándole la clave foránea del cliente.
     */
    @Transactional
    public Tarjeta guardarTarjetaParaCliente(Tarjeta tarjeta, Long idCliente) {
        if (!validarDatosTarjeta(tarjeta)) {
            throw new IllegalArgumentException("Los datos de la tarjeta son invalidos o estan incompletos.");
        }

        if (idCliente != null) {
            Cliente clienteManaged = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("El cliente especificado no existe."));
            
            tarjeta.setCliente(clienteManaged);
        }

        return tarjetaRepository.save(Objects.requireNonNull(tarjeta));
    }

    public Tarjeta buscarPorId(Long idTarjeta) {
        if (idTarjeta == null) return null;
        return tarjetaRepository.findById(idTarjeta).orElse(null);
    }

    public Tarjeta buscarPorNumeroTarjeta(String numeroTarjeta) {
        if (numeroTarjeta == null || numeroTarjeta.trim().isEmpty()) return null;
        return tarjetaRepository.findByNumeroTarjeta(numeroTarjeta.trim());
    }
}