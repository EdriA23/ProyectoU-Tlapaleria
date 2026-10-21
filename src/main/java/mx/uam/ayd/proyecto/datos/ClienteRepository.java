package mx.uam.ayd.proyecto.datos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

/**
 * Repositorio para la entidad Cliente (HU-12).
 */
@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long> {

    /**
     * Busca un cliente por su teléfono exacto (RN-01: teléfono único).
     *
     * @param telefono teléfono del cliente
     * @return el cliente o null si no existe
     */
    Cliente findByTelefono(String telefono);

    /**
     * Búsqueda rápida por nombre o teléfono (coincidencia parcial, sin distinguir mayúsculas).
     *
     * @param nombre fragmento del nombre
     * @param telefono fragmento del teléfono
     * @return lista de clientes que coinciden
     */
    List<Cliente> findByNombreContainingIgnoreCaseOrTelefonoContaining(String nombre, String telefono);

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