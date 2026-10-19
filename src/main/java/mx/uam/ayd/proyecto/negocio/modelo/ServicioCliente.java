package mx.uam.ayd.proyecto.negocio.modelo;

import mx.uam.ayd.proyecto.datos.ClienteRepository;

public class ServicioCliente {

    private final ClienteRepository clienteRepo;

    public ServicioCliente(ClienteRepository clienteRepo) {
        if (clienteRepo == null) {
            throw new IllegalArgumentException("El repositorio de clientes es obligatorio.");
        }
        this.clienteRepo = clienteRepo;
    }

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