
package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.CorteCajaRepository;
import mx.uam.ayd.proyecto.datos.VentaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.CorteCaja;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

/**
 * Servicio de la HU-15.
 * Consulta ventas y registra cortes sin modificar el módulo de ventas.
 */
@Service
public class ServicioCorteCaja {

    /** Repositorio de los cortes de caja. */
    private final CorteCajaRepository corteCajaRepository;

    /** Repositorio existente de ventas. */
    private final VentaRepository ventaRepository;

    /**
     * Constructor administrado por Spring.
     *
     * @param corteCajaRepository repositorio de cortes
     * @param ventaRepository repositorio existente de ventas
     */
    @Autowired
    public ServicioCorteCaja(
            CorteCajaRepository corteCajaRepository,
            VentaRepository ventaRepository) {

        this.corteCajaRepository = corteCajaRepository;
        this.ventaRepository = ventaRepository;
    }

    /**
     * Consulta las ventas guardadas cuya fecha es la del día actual.
     *
     * @return lista de ventas registradas hoy
     */
    public List<Venta> consultarVentasDelDia() {

        LocalDate hoy = LocalDate.now();

        // Recupera las ventas guardadas sin modificar sus registros.
        List<Venta> ventas = new ArrayList<>();
        ventaRepository.findAll().forEach(ventas::add);

        // Filtra las ventas correspondientes al día actual.
        return ventas.stream()
                .filter(venta -> venta.getDate() != null)
                .filter(venta ->
                        venta.getDate().toLocalDate().equals(hoy))
                .collect(Collectors.toList());
    }

    /**
     * Registra un corte nuevo. Se permiten varios cortes por día.
     *
     * @param corte información que se desea guardar
     * @return corte guardado en la base de datos
     */
    @Transactional
    public CorteCaja registrarCorte(CorteCaja corte) {

        if (corte == null) {
            throw new IllegalArgumentException(
                    "El corte de caja no puede ser nulo.");
        }

        if (corte.getNombreEmpleado() == null
                || corte.getNombreEmpleado().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Escribe el nombre del empleado.");
        }

        if (corte.getTotalVentas() < 0
                || corte.getEfectivoEsperado() < 0) {
            throw new IllegalArgumentException(
                    "Los importes no pueden ser negativos.");
        }

        // Asigna fecha y hora si no fueron proporcionadas.
        if (corte.getFechaHora() == null) {
            corte.setFechaHora(LocalDateTime.now());
        }

        // Genera una clave para esta operación, no para todo el día.
        if (corte.getTurno() == null
                || corte.getTurno().trim().isEmpty()) {
            corte.setTurno(
                    "CORTE-" + System.currentTimeMillis());
        }

        return corteCajaRepository.save(corte);
    }

    /**
     * Consulta todos los cortes registrados.
     *
     * @return lista de cortes guardados
     */
    public List<CorteCaja> consultarCortes() {

        List<CorteCaja> cortes = new ArrayList<>();
        corteCajaRepository.findAll().forEach(cortes::add);

        return cortes;
    }

    /**
     * Consulta los cortes por identificador numérico del vendedor.
     *
     * @param idVendedor identificador del vendedor
     * @return cortes encontrados
     */
    public List<CorteCaja> consultarCortesPorVendedor(
            int idVendedor) {

        return corteCajaRepository.findByIdVendedor(idVendedor);
    }
}
