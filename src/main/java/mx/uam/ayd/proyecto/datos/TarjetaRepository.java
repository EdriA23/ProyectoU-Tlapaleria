package mx.uam.ayd.proyecto.datos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import mx.uam.ayd.proyecto.negocio.modelo.Tarjeta;

@Repository
public interface TarjetaRepository extends CrudRepository<Tarjeta, Long> {

    // Método para buscar tarjetas por número (útil para validar si una tarjeta ya existe)
    List<Tarjeta> findByNumeroTarjeta(String numeroTarjeta);
}