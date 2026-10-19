package mx.uam.ayd.proyecto.negocio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.ClienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

/**
 * Servicio encargado de la lógica de negocio de los clientes,
 * incluyendo su registro, búsqueda y validación de datos para domicilios (HU-17).
 */
@Service
public class ServicioCliente {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Valida que los datos obligatorios de la dirección y del cliente 
     * no estén vacíos o nulos para cumplir con los requisitos de la HU-17.
     */
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

    /**
     * Guarda o actualiza un cliente en la base de datos utilizando el repositorio.
     */
    @Transactional
    public Cliente guardarCliente(Cliente cliente) {
        if (!validarDatosCliente(cliente)) {
            throw new IllegalArgumentException("No se puede guardar el cliente: faltan datos obligatorios.");
        }
        return clienteRepository.save(cliente);
    }

    /**
     * Busca un cliente por su identificador único.
     */
    public Cliente buscarClientePorId(long idCliente) {
        return clienteRepository.findById(idCliente);
    }

    /**
     * Busca un cliente registrado por su número telefónico.
     */
    public Cliente buscarClientePorTelefono(String telefono) {
        return clienteRepository.findByTelefono(telefono);
    }
}