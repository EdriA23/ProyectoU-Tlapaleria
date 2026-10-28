package mx.uam.ayd.proyecto.datos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Tarjeta;

@Repository
public interface TarjetaRepository extends CrudRepository<Tarjeta, Long> {

    Tarjeta findByNumeroTarjeta(String numeroTarjeta);
    
    // Consulta todas las tarjetas de un cliente
    List<Tarjeta> findByCliente(Cliente cliente);
}