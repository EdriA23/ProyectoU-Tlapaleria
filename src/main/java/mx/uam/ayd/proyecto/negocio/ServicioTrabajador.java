package mx.uam.ayd.proyecto.negocio;

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
    private TrabajadorRepository repositoryTrabajador;

    /**
     * Recupera todos los trabajadores registrados.
     * Invoca recuperarTodos() para coincidir con el Diagrama de Secuencia.
     * 
     * @return Lista con todos los trabajadores
     */
    public List<Trabajador> recuperarTrabajador() {
        return repositoryTrabajador.findAll();
    }

    /**
     * Genera un ID automático para el formulario.
     */
    public String generarIdTrabajador() {
        long cuenta = repositoryTrabajador.count() + 1;
        return String.format("EMP-%03d", cuenta);
    }

    /**
     * Crea la instancia de Trabajador y la guarda en la base de datos.
     * Coincide con la secuencia: new(...) -> save(...) / guardarTrabajador(...)
     */
    public Trabajador creaTrabajador(String idTrabajador, String nombre, String telefono, String rol, String correo) {
        // Instancia la entidad usando la firma refactorizada con idTrabajador
        Trabajador trabajador = new Trabajador(nombre, telefono, rol, correo, idTrabajador);

        // Persiste la entidad en el repositorio
        return repositoryTrabajador.save(trabajador);
    }
    /**
     * Obtiene los detalles de un trabajador a partir de su ID.
     */
    public Trabajador obtenerDetalles(Long id) {
        return repositoryTrabajador.findById(id).orElse(null);
    }
}