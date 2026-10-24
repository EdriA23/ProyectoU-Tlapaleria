package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Objects;

@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long> {

    default Cliente guardar(Cliente cliente) {
        return save(Objects.requireNonNull(cliente));
    }

    default Cliente buscarPorId(int idCliente) {
        return findById((long) idCliente).orElse(null);
    }

    List<Cliente> findByTelefono(String telefono);
}