package mx.uam.ayd.proyecto.datos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Trabajador;


public interface TrabajadorRepository extends CrudRepository<Trabajador, Long> {

    /**
     * Recupera todos los trabajadores registrados.
     * 
     * @return Lista con todos los trabajadores
     */
    List<Trabajador> findAll();

    /**
     * Permite filtrar trabajadores según su rol o puesto en la tienda.
     */
    List<Trabajador> findByRol(String rol);
}