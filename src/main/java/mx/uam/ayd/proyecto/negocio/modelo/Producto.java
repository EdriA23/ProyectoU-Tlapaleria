package mx.uam.ayd.proyecto.negocio.modelo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/**
 * Entidad de negocio Producto
 *
 * @author Yamelin, Guillermo, Dydier, Yael, Sheyla
 */
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idProducto; // Identificador único del producto en la base de datos (indispensable para buscarlo en HU-09 y HU-10)

    private String clave;

    private String nombre; // Nombre del producto (ej. martillo, desarmador)

    private String tipoProducto;

    /**
     * Usado para HU-09, se guarda el precio actual del producto que el usuario modifica.
     */
    private Double precio;

    private String marca;

    private String categoria;

    private Double precioCompra;

    /**
     * Stock o piezas disponibles (se usa para la HU-10 cuando descuentas lo que se devuelve por dañado).
     */
    private int existenciaActual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inventario")
    private Inventario inventario;

    /**
     * Relación con el historial: guarda los movimientos que genera
     * la HU-09 (cambio de precio) y la HU-10 (devoluciones).
     */
    @OneToMany(mappedBy = "producto")
    private List<MovimientoInventario> movimientosInventario = new ArrayList<>();

    @OneToMany(mappedBy = "producto")
    private List<DescripcionVenta> descripcionesVenta = new ArrayList<>();

    /**
     * Relación para la HU-10: lista de devoluciones asociadas a este producto.
     */
    @OneToMany(mappedBy = "producto")
    private List<Devolucion> devoluciones = new ArrayList<>();

    @OneToMany(mappedBy = "producto")
    private List<Pedido> pedidos = new ArrayList<>();

    public Producto() {
    }

   // GETTERS

    /** Obtiene el ID del producto (usado en HU-09 y HU-10 para identificarlo) */
    public long getIdProducto() {
        return idProducto;
    }

    public String getClave() {
        return clave;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipoProducto() {
        return tipoProducto;
    }

    /** Obtiene el precio actual (usado en HU-09) */
    public Double getPrecio() {
        return precio;
    }

    public String getMarca() {
        return marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    /** Obtiene el stock actual (usado en HU-10 para validar devoluciones) */
    public int getExistenciaActual() {
        return existenciaActual;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public List<MovimientoInventario> getMovimientosInventario() {
        return movimientosInventario;
    }

    public List<DescripcionVenta> getDescripcionesVenta() {
        return descripcionesVenta;
    }

    public List<Devolucion> getDevoluciones() {
        return devoluciones;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    // SETTERS

    /** Asigna el ID del producto */
    public void setIdProducto(long idProducto) {
        this.idProducto = idProducto;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    /** Guarda el nuevo precio actualizado (usado en HU-09) */
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }

    /** Actualiza el stock restando las piezas dañadas (usado en HU-10) */
    public void setExistenciaActual(int existenciaActual) {
        this.existenciaActual = existenciaActual;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public void setMovimientosInventario(List<MovimientoInventario> movimientosInventario) {
        this.movimientosInventario = movimientosInventario;
    }

    public void setDescripcionesVenta(List<DescripcionVenta> descripcionesVenta) {
        this.descripcionesVenta = descripcionesVenta;
    }

    public void setDevoluciones(List<Devolucion> devoluciones) {
        this.devoluciones = devoluciones;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Producto other = (Producto) obj;
        return idProducto == other.idProducto;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(idProducto);
    }

    @Override
    public String toString() {
        return "Producto [idProducto=" + idProducto
                + ", clave='" + clave + '\''
                + ", nombre='" + nombre + '\''
                + ", precio=" + precio
                + ", stock=" + existenciaActual + "]";
    }
}