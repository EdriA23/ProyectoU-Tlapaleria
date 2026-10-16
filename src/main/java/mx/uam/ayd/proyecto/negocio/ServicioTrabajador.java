package mx.uam.ayd.proyecto.negocio;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.uam.ayd.proyecto.datos.TrabajadorRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Trabajador;

/**
 * Servicio para la gestión de Trabajadores.
 */
@Service
public class ServicioTrabajador {

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    /**
     * Recupera todos los trabajadores registrados.
     * 
     * @return Lista con todos los trabajadores
     */
    public List<Trabajador> recuperaTrabajadores() {
        List<Trabajador> trabajadores = new ArrayList<>();
        trabajadorRepository.findAll().forEach(trabajadores::add);
        return trabajadores;
    }
}