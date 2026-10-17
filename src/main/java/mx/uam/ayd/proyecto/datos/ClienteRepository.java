package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

public class ClienteRepository {

    public void guardar(Cliente cliente) {
        // Lógica de conexión a Base de Datos (INSERT)
        System.out.println("Cliente guardado en la base de datos.");
    }

    public Cliente buscarPorId(int idCliente) {
        // Lógica de consulta (SELECT * FROM cliente WHERE id_cliente = ?)
        return new Cliente();
    }
}
