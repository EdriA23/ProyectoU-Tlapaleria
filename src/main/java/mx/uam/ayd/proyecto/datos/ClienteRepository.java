package mx.uam.ayd.proyecto.datos;

import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long> {

    /**
     * Conserva la firma del método 'guardar' original
     * para no romper otras clases o servicios del proyecto.
     */
    default Cliente guardar(Cliente cliente) {
        return save(cliente);
    }

    /**
     * Conserva la búsqueda por ID adaptada a CrudRepository.
     */
    default Cliente buscarPorId(int idCliente) {
        return findById((long) idCliente).orElse(null);
    }

    /**
     * Método automático de Spring Data JPA para buscar por teléfono.
     */
    Optional<Cliente> findByTelefono(String telefono);
}