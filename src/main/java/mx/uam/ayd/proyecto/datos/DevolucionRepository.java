package mx.uam.ayd.proyecto.datos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import mx.uam.ayd.proyecto.negocio.modelo.Devolucion;

/**
 * Repositorio para la gestión de Devoluciones (HU-10).
 *
 * @author Yamelin Larios Nepomuseno
 */
public interface DevolucionRepository extends CrudRepository<Devolucion, Long> {

    /**
     * Encuentra todas las devoluciones asociadas a un producto por su identificador.
     *
     * Devolucion -> producto -> idProducto
     *
     * @param idProducto identificador del producto
     * @return lista de devoluciones
     */
    List<Devolucion> findByProducto_IdProducto(Long idProducto);

}

//HOLA AMIGOS ASUSTADORES
