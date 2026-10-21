package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidad de negocio Cliente.
 * HU-12 - Registrar clientes para darles un descuento de fidelidad.
 */
@Entity
public class Cliente {

    /** RN-02: descuento de fidelidad inicial (porcentaje). */
    public static final double DESCUENTO_FIDELIDAD_INICIAL = 5.0;

    /** Número de cliente único, generado automáticamente. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idCliente;

    private String nombre;

    /** RN-01: el teléfono debe ser único en el sistema. */
    @Column(unique = true)
    private String telefono;

    private String correoElectronico;
    private String direccion;
    private String numeroCasa;
    private String entreCalles;
    private String colorCasa;

    /*
     * Se usa Double (nullable) para no romper registros que ya existen
     * en la base de datos cuando Hibernate agregue la columna.
     */
    private Double descuentoFidelidad = DESCUENTO_FIDELIDAD_INICIAL;

    public Cliente() {}

    public Cliente(String nombre, String telefono, String direccion, String numeroCasa, String entreCalles, String colorCasa) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.numeroCasa = numeroCasa;
        this.entreCalles = entreCalles;
        this.colorCasa = colorCasa;
    }

    public long getIdCliente() { return idCliente; }
    public void setIdCliente(long idCliente) { this.idCliente = idCliente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getNumeroCasa() { return numeroCasa; }
    public void setNumeroCasa(String numeroCasa) { this.numeroCasa = numeroCasa; }

    public String getEntreCalles() { return entreCalles; }
    public void setEntreCalles(String entreCalles) { this.entreCalles = entreCalles; }

    public String getColorCasa() { return colorCasa; }
    public void setColorCasa(String colorCasa) { this.colorCasa = colorCasa; }

    public Double getDescuentoFidelidad() { return descuentoFidelidad; }
    public void setDescuentoFidelidad(Double descuentoFidelidad) { this.descuentoFidelidad = descuentoFidelidad; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Cliente other = (Cliente) obj;
        return idCliente == other.idCliente;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(idCliente);
    }

    @Override
    public String toString() {
        return "Cliente [idCliente=" + idCliente
                + ", nombre=" + nombre
                + ", telefono=" + telefono
                + ", correoElectronico=" + correoElectronico
                + ", descuentoFidelidad=" + descuentoFidelidad + "]";
    }
}