package mx.uam.ayd.proyecto.negocio.modelo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Entidad de negocio Trabajador
 *
 * @author javitos
 */
@Entity
public class Trabajador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idTrabajador;

    private String nombreCompleto;

    private String rol;

    private String telefono;
   
    private String correoElectronico;

    private double salario;

    private String numeroTrabajador;

    /*
     * Un vendedor puede registrar muchas devoluciones.
     *
     * mappedBy = "vendedor" hace referencia al atributo
     * vendedor existente dentro de Devolucion.
     */
    @OneToMany(mappedBy = "trabajador")
    private List<Devolucion> devoluciones = new ArrayList<>();

    /**
     * Constructor vacío requerido por JPA.
     */
    public Trabajador() {
    }

    public long getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(long idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRol(){
        return rol;
    }

    public void setRol(String rol){
        this.rol=rol;
    }
   
    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public String getNumeroTrabajador() {
        return numeroTrabajador;
    }

    public void setNumeroTrabajador(String numeroTrabajador) {
        this.numeroTrabajador = numeroTrabajador;
    }

    public List<Devolucion> getDevoluciones() {
        return devoluciones;
    }

    public void setDevoluciones(
            List<Devolucion> devoluciones) {

        this.devoluciones = devoluciones;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Trabajador other = (Trabajador) obj;

        return idTrabajador == other.idTrabajador;
    }

    @Override
    public int hashCode() {
        return (int) (31 * idTrabajador);
    }

    @Override
    public String toString() {
        return "Trabajador [idTrabajador=" + idTrabajador
                + ", nombreCompleto=" + nombreCompleto
                + ", rol=" + rol
                + ", telefono=" + telefono
                + ", correoElectronico=" + correoElectronico
                + ", salario=" + salario
                + ", numeroTrabajador=" + numeroTrabajador + "]";
    }
}