package mx.uam.ayd.proyecto.datos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

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
}