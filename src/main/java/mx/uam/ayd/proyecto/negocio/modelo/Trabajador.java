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
    private long idEmpleado;

    private String nombreCompleto;

    private int age;

    private String rol;

    private String telefono;
   
    private String correoElectronico;

    private double salario;

    private String numeroEmpleado;

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

    public long getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(long idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public int getEdad() {
        return age;
    }

    public void setEdad(int edad) {
        this.age = edad;
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

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
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

        return idEmpleado == other.idEmpleado;
    }

    @Override
    public int hashCode() {
        return (int) (31 * idEmpleado);
    }

    @Override
    public String toString() {
        return "Trabajador [idEmpleado=" + idEmpleado
                + ", nombreCompleto=" + nombreCompleto
                + ", rol=" + rol
                + ", telefono=" + telefono
                + ", correoElectronico=" + correoElectronico
                + ", salario=" + salario
                + ", numeroEmpleado=" + numeroEmpleado + "]";
    }
}