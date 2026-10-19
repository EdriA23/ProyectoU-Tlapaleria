package mx.uam.ayd.proyecto.negocio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // <--- Importante
import org.springframework.transaction.annotation.Transactional;
import mx.uam.ayd.proyecto.datos.ClienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

@Service // <--- Esta anotación es la que le dice a Spring que cree el bean
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
        return clienteRepository.save(cliente);
    }

    public void registrarCliente(Cliente cliente) {
        guardarCliente(cliente);
    }

    public Cliente buscarClientePorId(long idCliente) {
        return clienteRepository.findById(idCliente).orElse(null);
    }

    public Cliente buscarClientePorTelefono(String telefono) {
        return clienteRepository.findByTelefono(telefono).orElse(null);
    }
}