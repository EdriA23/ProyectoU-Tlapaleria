package mx.uam.ayd.proyecto.negocio.modelo;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Entidad de negocio Cliente.
 */
@Entity
public class Cliente {

    public static final double DESCUENTO_FIDELIDAD_INICIAL = 5.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idCliente;

    private String nombre;
    private String telefono;
    private String direccion;
    private String numeroCasa;
    private String entreCalles;
    private String colorCasa;
    private String correoElectronico;
    private Double descuentoFidelidad;

    // Relación 1 Cliente a N Tarjetas con carga EAGER para JavaFX
    @OneToMany(mappedBy = "cliente", fetch = FetchType.EAGER)
    private List<Tarjeta> tarjetas = new ArrayList<>();

    public Cliente() {}

    public Cliente(String nombre, String telefono, String direccion, String numeroCasa, String entreCalles, String colorCasa) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.numeroCasa = numeroCasa;
        this.entreCalles = entreCalles;
        this.colorCasa = colorCasa;
    }

    public List<Tarjeta> getTarjetas() { return tarjetas; }
    public void setTarjetas(List<Tarjeta> tarjetas) { this.tarjetas = tarjetas; }

    /**
     * Método helper para agregar una tarjeta al cliente y sincronizar la relación bidireccional.
     */
    public boolean agregarTarjeta(Tarjeta tarjeta) {
        if (tarjeta != null) {
            tarjeta.setCliente(this);
            return this.tarjetas.add(tarjeta);
        }
        return false;
    }

    public long getIdCliente() { return idCliente; }
    public void setIdCliente(long idCliente) { this.idCliente = idCliente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getNumeroCasa() { return numeroCasa; }
    public void setNumeroCasa(String numeroCasa) { this.numeroCasa = numeroCasa; }

    public String getEntreCalles() { return entreCalles; }
    public void setEntreCalles(String entreCalles) { this.entreCalles = entreCalles; }

    public String getColorCasa() { return colorCasa; }
    public void setColorCasa(String colorCasa) { this.colorCasa = colorCasa; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public Double getDescuentoFidelidad() { return descuentoFidelidad; }
    public void setDescuentoFidelidad(Double descuentoFidelidad) { this.descuentoFidelidad = descuentoFidelidad; }

    @Override
    public String toString() {
        return "Cliente [idCliente=" + idCliente + ", nombre=" + nombre + "]";
    }
}