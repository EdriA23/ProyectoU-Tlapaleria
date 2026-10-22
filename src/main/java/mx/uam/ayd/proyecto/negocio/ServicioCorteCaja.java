
package mx.uam.ayd.proyecto.negocio;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.CorteCajaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.CorteCaja;

/**
 * Servicio encargado de las reglas de negocio de la HU-15.
 */
@Service
public class ServicioCorteCaja {

    private final CorteCajaRepository corteCajaRepository;

    @Autowired
    public ServicioCorteCaja(
            CorteCajaRepository corteCajaRepository) {
        this.corteCajaRepository = corteCajaRepository;
    }

    /**
     * Registra un corte de caja si todavía no existe uno
     * para el turno indicado.
     *
     * @param corte datos del corte que se desea registrar
     * @return corte guardado
     */
    @Transactional
    public CorteCaja registrarCorte(CorteCaja corte) {

        if (corte == null) {
            throw new IllegalArgumentException(
                    "El corte de caja no puede ser nulo.");
        }

        if (corte.getTurno() == null
                || corte.getTurno().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe especificarse el turno del corte.");
        }

        List<CorteCaja> cortesDelTurno =
                corteCajaRepository.findByTurno(corte.getTurno());

        if (!cortesDelTurno.isEmpty()) {
            throw new IllegalStateException(
                    "Ya existe un corte de caja para este turno.");
        }

        if (corte.getFechaHora() == null) {
            corte.setFechaHora(LocalDateTime.now());
        }

        if (corte.getTotalVentas() < 0
                || corte.getEfectivoEsperado() < 0) {
            throw new IllegalArgumentException(
                    "Los importes del corte no pueden ser negativos.");
        }

        return corteCajaRepository.save(corte);
    }

    /**
     * Consulta todos los cortes registrados.
     *
     * @return lista de cortes
     */
    public List<CorteCaja> consultarCortes() {
        return (List<CorteCaja>) corteCajaRepository.findAll();
    }

    /**
     * Consulta los cortes de un vendedor.
     *
     * @param idVendedor identificador del vendedor
     * @return lista de cortes
     */
    public List<CorteCaja> consultarCortesPorVendedor(
            int idVendedor) {
        return corteCajaRepository.findByIdVendedor(idVendedor);
    }
}
