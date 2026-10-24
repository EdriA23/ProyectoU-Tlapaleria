package mx.uam.ayd.proyecto.datos;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

public interface VentaRepository extends CrudRepository<Venta, Long> {
    
    // Método para filtrar pedidos a domicilio que aún no han sido entregados
    List<Venta> findByEsDomicilioTrueAndEstadoEntregaNot(String estadoEntrega);
}