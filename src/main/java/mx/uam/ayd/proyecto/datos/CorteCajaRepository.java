
package mx.uam.ayd.proyecto.datos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import mx.uam.ayd.proyecto.negocio.modelo.CorteCaja;

/**
 * Repositorio para almacenar y consultar los cortes de caja.
 */
public interface CorteCajaRepository
        extends CrudRepository<CorteCaja, Long> {

    /**
     * Consulta los cortes realizados por un vendedor.
     *
     * @param idVendedor identificador del vendedor
     * @return lista de cortes del vendedor
     */
    List<CorteCaja> findByIdVendedor(int idVendedor);

    /**
     * Consulta los cortes de un turno.
     *
     * @param turno identificador del turno
     * @return lista de cortes del turno
     */
    List<CorteCaja> findByTurno(String turno);
}
