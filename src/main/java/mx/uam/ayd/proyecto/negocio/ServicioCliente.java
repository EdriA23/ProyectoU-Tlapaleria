package mx.uam.ayd.proyecto.negocio;

import mx.uam.ayd.proyecto.datos.ClienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

public class ServicioCliente {

    private ClienteRepository clienteRepo = new ClienteRepository();

    public boolean validarDatosCliente(Cliente cliente) {
        if (cliente == null) return false;
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) return false;
        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) return false;
        if (cliente.getDireccion() == null || cliente.getDireccion().trim().isEmpty()) return false;
        if (cliente.getNumeroCasa() == null || cliente.getNumeroCasa().trim().isEmpty()) return false;
        if (cliente.getEntreCalles() == null || cliente.getEntreCalles().trim().isEmpty()) return false;
        if (cliente.getColorCasa() == null || cliente.getColorCasa().trim().isEmpty()) return false;
        return true;
    }

    public void registrarCliente(Cliente cliente) {
        if (validarDatosCliente(cliente)) {
            clienteRepo.guardar(cliente);
        } else {
            throw new IllegalArgumentException("Los datos obligatorios del cliente están incompletos.");
        }
    }
}