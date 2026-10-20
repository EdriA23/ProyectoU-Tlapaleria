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
    private TrabajadorRepository repositoryTrabajador;

    /**
     * Recupera todos los trabajadores registrados.
     * 
     * @return Lista con todos los trabajadores
     */
    public List<Trabajador> recuperarTrabajador() {
        return repositoryTrabajador.findAll();
    }
    /**
     * Genera un ID automático para el formulario
     */
    public String generarIdTrabajador() {
        long cuenta = repositoryTrabajador.count() + 1;
        return String.format("EMP-%03d", cuenta);
    }
    /**
     * 
     * Crea la instancia de Trabajador y la guarda en la base de datos.
     */
    public Trabajador creaTrabajador(String nombre, String telefono, String rol, String correo) {
        // Instancia el objeto Trabajador: new(nombre, telefono, rol, correo)
        Trabajador trabajador = new Trabajador();
        trabajador.setNombreCompleto(nombre);
        trabajador.setTelefono(telefono);
        trabajador.setRol(rol);
        trabajador.setCorreoElectronico(correo);
        
        // Asigna el número de trabajador 
        trabajador.setNumeroTrabajador(generarIdTrabajador());

        // guarda el trabajador en el repositorio
        return repositoryTrabajador.save(trabajador);
    }
}