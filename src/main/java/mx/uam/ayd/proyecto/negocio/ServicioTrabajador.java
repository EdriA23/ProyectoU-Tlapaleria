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
     * 
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
     * 
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
       /**
 * Guarda o actualiza un trabajador existente.
 */
public Trabajador guardar(Trabajador trabajador) {
    return repositoryTrabajador.save(trabajador);
}
    /**
     * Elimina a un trabajador por su ID.
     */
    public boolean eliminarTrabajador(Long id) {
        if (repositoryTrabajador.existsById(id)) {
            repositoryTrabajador.deleteById(id);
            return true;
        }
        return false;
    }
/**
 * Busca trabajadores cuyo nombre contenga el texto ingresado.
 * 
 */
public List<Trabajador> buscarTrabajadores(String criterio) {
    if (criterio == null || criterio.trim().isEmpty()) {
        return recuperarTrabajador();
    }

    String filtro = criterio.trim().toLowerCase();
    List<Trabajador> todos = repositoryTrabajador.findAll();
    List<Trabajador> resultados = new java.util.ArrayList<>();

    for (Trabajador t : todos) {
        if (t.getNombreCompleto() != null && t.getNombreCompleto().toLowerCase().contains(filtro)) {
            resultados.add(t);
        }
    }

    return resultados;
}
  
}