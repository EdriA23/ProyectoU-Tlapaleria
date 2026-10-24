package mx.uam.ayd.proyecto.datos;

import java.util.List;
import java.util.Objects;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long> {

    default Cliente guardar(Cliente cliente) {
        return save(Objects.requireNonNull(cliente));
    }

    default Cliente buscarPorId(int idCliente) {
        return findById((long) idCliente).orElse(null);
    }

    List<Cliente> findByTelefono(String telefono);

    /**
     * Búsqueda rápida por nombre o teléfono (coincidencia parcial, sin distinguir mayúsculas). HU-12.
     *
     * @param nombre fragmento del nombre
     * @param telefono fragmento del teléfono
     * @return lista de clientes que coinciden
     */
    List<Cliente> findByNombreContainingIgnoreCaseOrTelefonoContaining(String nombre, String telefono);
}