package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Entidad de negocio Cliente.
 * Usada por pedidos a domicilio, tarjetas y por la HU-12 (registro de clientes con descuento de fidelidad).
 */
@Entity
public class Cliente {

    /** RN-02 (HU-12): descuento de fidelidad inicial, en porcentaje. */
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

    // --- Campos agregados por la HU-12 ---
    private String correoElectronico;

    /*
     * Se usa Double (nullable) para no romper registros existentes.
     * Solo se asigna al registrar el cliente desde la HU-12.
     */
    private Double descuentoFidelidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tarjeta_fk", nullable = true)
    private Tarjeta tarjeta;

    public Cliente() {}

    public Cliente(String nombre, String telefono, String direccion, String numeroCasa, String entreCalles, String colorCasa) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.numeroCasa = numeroCasa;
        this.entreCalles = entreCalles;
        this.colorCasa = colorCasa;
    }

    public Tarjeta getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(Tarjeta tarjeta) {
        this.tarjeta = tarjeta;
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
        return "Cliente [idCliente=" + idCliente
                + ", nombre=" + nombre
                + ", telefono=" + telefono
                + ", correoElectronico=" + correoElectronico
                + ", descuentoFidelidad=" + descuentoFidelidad + "]";
    }
}