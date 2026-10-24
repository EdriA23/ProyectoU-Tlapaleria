package mx.uam.ayd.proyecto.negocio;

import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mx.uam.ayd.proyecto.datos.ClienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Tarjeta;

@Service
public class ServicioCliente {

    @Autowired
    private ClienteRepository clienteRepository;

    public boolean validarDatosCliente(Cliente cliente) {
        if (cliente == null) {
            return false;
        }

        String nombre = cliente.getNombre();
        String telefono = cliente.getTelefono();
        String direccion = cliente.getDireccion();
        String numeroCasa = cliente.getNumeroCasa();
        String entreCalles = cliente.getEntreCalles();
        String colorCasa = cliente.getColorCasa();

        if (nombre == null || nombre.trim().isEmpty()) return false;
        if (telefono == null || telefono.trim().isEmpty()) return false;
        if (direccion == null || direccion.trim().isEmpty()) return false;
        if (numeroCasa == null || numeroCasa.trim().isEmpty()) return false;
        if (entreCalles == null || entreCalles.trim().isEmpty()) return false;
        if (colorCasa == null || colorCasa.trim().isEmpty()) return false;

        return true;
    }

    @Transactional
    public Cliente guardarCliente(Cliente cliente) {
        if (!validarDatosCliente(cliente)) {
            throw new IllegalArgumentException("No se puede guardar el cliente: faltan datos obligatorios.");
        }
        return clienteRepository.save(Objects.requireNonNull(cliente));
    }

    public void registrarCliente(Cliente cliente) {
        guardarCliente(cliente);
    }

    public Cliente buscarClientePorId(long idCliente) {
        return clienteRepository.findById(idCliente).orElse(null);
    }

    public Cliente buscarClientePorTelefono(String telefono) {
        List<Cliente> clientes = clienteRepository.findByTelefono(telefono);
        if (clientes != null && !clientes.isEmpty()) {
            return clientes.get(0);
        }
        return null;
    }

    @Transactional
    public Cliente asignarTarjetaACliente(long idCliente, Tarjeta tarjeta) {
        Cliente cliente = buscarClientePorId(idCliente);
        if (cliente != null && tarjeta != null) {
            cliente.setTarjeta(tarjeta);
            return clienteRepository.save(Objects.requireNonNull(cliente));
        }
        return null;
    }
}