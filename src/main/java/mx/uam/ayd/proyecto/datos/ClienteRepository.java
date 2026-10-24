package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // <-- Importante para usar List

@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long> {

    default Cliente guardar(Cliente cliente) {
        return save(cliente);
    }

    default Cliente buscarPorId(int idCliente) {
        return findById((long) idCliente).orElse(null);
    }

    // Modificado para regresar una lista y evitar el error de múltiples resultados
    List<Cliente> findByTelefono(String telefono);
}