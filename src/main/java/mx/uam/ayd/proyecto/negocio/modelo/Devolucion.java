package mx.uam.ayd.proyecto.negocio.modelo;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Entidad de negocio Devolucion
 * HU-10 - Devolución de productos dañados
 *
 * @author Yamelin Larios Nepomuseno
 */
@Entity
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idDevolucion; // Identificador único de la devolución en la base de datos

    /**
     * Muchas devoluciones pueden corresponder al mismo producto.
     * nos sirve en la HU-10 para saber a qué artículo se le descontarán las piezas dañadas.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    private int cantidad; // Número de piezas devueltas por defecto (clave para restar al stock en la HU-10)

    private String motivo; // Razón de la devolución (ej. "Empaque roto", "Defecto de fábrica")

    private String tipoDevolucion; // Especifica si va a proveedor, merma, etc.

    private LocalDateTime fecha; // Fecha y hora exacta de cuándo se registró la devolución

    /**
     * Se conserva este atributo por compatibilidad con el sistema base.
     */
    private String numeroEmpleado;

    /**
     * Muchas devoluciones pueden ser registradas por el mismo Trabajador.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_trabajador")
    private Trabajador trabajador;

    /**
     * Muchas devoluciones pueden asociarse a un mismo proveedor.
     * Mapeado para resolver el 'mappedBy' de la entidad Proveedor.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    // Constructor por defecto requerido por JPA
    public Devolucion() {
    }

    // MÉTODOS DE ACCESO: GETTERS

    /** Obtiene el ID único de la devolución */
    public long getIdDevolucion() {
        return idDevolucion;
    }

    /** Obtiene el producto asociado a la devolución (usado en HU-10) */
    public Producto getProducto() {
        return producto;
    }

    /** Obtiene la cantidad de piezas devueltas (usado para actualizar existencias en HU-10) */
    public int getCantidad() {
        return cantidad;
    }

    /** Obtiene el motivo de la devolución */
    public String getMotivo() {
        return motivo;
    }

    /** Obtiene el tipo de devolución */
    public String getTipoDevolucion() {
        return tipoDevolucion;
    }

    /** Obtiene la fecha del registro */
    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public Trabajador getTrabajador() {
        return trabajador;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    // MÉTODOS DE ACCESO: SETTERS

    /** Asigna el ID de la devolución */
    public void setIdDevolucion(long idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    /** Asocia un producto a la devolución (clave en HU-10) */
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    /** Asigna la cantidad de piezas afectadas */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /** Guarda el motivo o razón del daño */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /** Define el tipo de devolución */
    public void setTipoDevolucion(String tipoDevolucion) {
        this.tipoDevolucion = tipoDevolucion;
    }

    /** Asigna la fecha y hora */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    // MÉTODOS SOBREESCRITOS (@Override)

    /**
     * Valida si dos objetos Devolucion son idénticos comparando su ID único,
     * permitiendo que las listas y colecciones de Spring/JavaFX los distingan correctamente.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Devolucion other = (Devolucion) obj;
        return idDevolucion == other.idDevolucion;
    }

    /**
     * Genera un código hash único basado en el ID de la devolución 
     * para optimizar búsquedas en estructuras de datos de Java.
     */
    @Override
    public int hashCode() {
        return Long.hashCode(idDevolucion);
    }

    /**
     * Devuelve una representación en texto de la devolución, útil para depuración
     * e impresión rápida en consola durante pruebas de la HU-10.
     */
    @Override
    public String toString() {
        return "Devolucion [idDevolucion=" + idDevolucion
                + ", idProducto=" + (producto != null ? producto.getIdProducto() : null)
                + ", cantidad=" + cantidad
                + ", motivo=" + motivo
                + ", tipoDevolucion=" + tipoDevolucion
                + ", fecha=" + fecha
                + ", numeroEmpleado=" + numeroEmpleado + "]";
    }
}