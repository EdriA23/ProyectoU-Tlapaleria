package mx.uam.ayd.proyecto.negocio.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import mx.uam.ayd.proyecto.presentacion.historialMovimientos.ControlHistorialMovimientos;

/**
 * @author Kevin Dydier López Flores
 */
@Entity
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idVenta;

    private Double total = 0.0;

    private LocalDateTime date;

    /*
     * Se conserva como atributo normal para no afectar
     * servicios, controladores o pruebas existentes.
     */
    private long idTrabajador;

    private double montoRecibido;

    private double cambio;

    /*
     * es mi hu-17
     */
    private boolean esDomicilio = false;

    @ManyToOne
    @JoinColumn(name = "idCliente")
    private Cliente cliente;

    private double costoEnvio = 0.0;

    private String estadoEntrega;

    private String repartidorAsignado;


    @OneToMany(
            mappedBy = "venta",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<DescripcionVenta> productos = new ArrayList<>();

    public Venta() {
    }

    /**
     * Agrega un producto a la venta y actualiza el total.
     * Permite cumplir con la HU-05 validando que el producto exista 
     * y la cantidad sea mayor a 0.
     *
     * @param producto producto a agregar
     * @param cantidad cantidad de piezas
     * @return true si se agrego con exito, false si los datos no son validos
     */
    public boolean agregaProducto(Producto producto, int cantidad) {

        if (producto == null || cantidad <= 0) {
            return false;
        }

        DescripcionVenta detalle = new DescripcionVenta(producto, cantidad);

        /*
         * Relacion con la venta
         */
        detalle.setVenta(this);

        this.productos.add(detalle);

        calculaTotal();

        return true;
    }

    /**
     * Suma los precios y cantidades de cada producto 
     * para calcular el total de la venta (HU-05), considerando opcionalmente el costo de envío.
     */
    public void calculaTotal() {

        double acumulado = 0.0;

        for (DescripcionVenta detalle : productos) {

            if (detalle.getProducto() != null) {

                acumulado += detalle.getProducto().getPrecio() * detalle.getCantidad();
            }
        }

        // Si es a domicilio, sumamos el costo de envío al total general
        this.total = acumulado + this.costoEnvio;
    }

    public void agregarDetalle(DescripcionVenta detalle) {

        if (detalle != null) {
            detalle.setVenta(this);
            this.productos.add(detalle);
        }
    }

    public void addDetalle(DescripcionVenta detalle) {
        this.agregarDetalle(detalle);
    }

    public void setFecha(LocalDateTime fecha) {
        this.date = fecha;
    }

    public void setPago(double pago) {
        this.montoRecibido = pago;
    }

    public long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(long idVenta) {
        this.idVenta = idVenta;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public long getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(long idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public double getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(double montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public double getCambio() {
        return cambio;
    }

    public void setCambio(double cambio) {
        this.cambio = cambio;
    }

   

    public boolean isEsDomicilio() {
        return esDomicilio;
    }

    public void setEsDomicilio(boolean esDomicilio) {
        this.esDomicilio = esDomicilio;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public double getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(double costoEnvio) {
        this.costoEnvio = costoEnvio;
        calculaTotal(); 
    }

    public String getEstadoEntrega() {
        return estadoEntrega;
    }

    public void setEstadoEntrega(String estadoEntrega) {
        this.estadoEntrega = estadoEntrega;
    }

    public String getRepartidorAsignado() {
        return repartidorAsignado;
    }

    public void setRepartidorAsignado(String repartidorAsignado) {
        this.repartidorAsignado = repartidorAsignado;
    }

    public List<DescripcionVenta> getProductos() {
        return productos;
    }

    public void setProductos(
            List<DescripcionVenta> productos) {

        this.productos = productos;

        if (this.productos != null) {

            for (DescripcionVenta detalle
                    : this.productos) {

                if (detalle != null) {
                    detalle.setVenta(this);
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null
                || getClass() != obj.getClass()) {

            return false;
        }

        Venta other = (Venta) obj;

        return idVenta == other.idVenta;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(idVenta);
    }

    @Override
    public String toString() {
        return "Venta{"
                + "id=" + idVenta
                + ", total=" + total
                + ", pago=" + montoRecibido
                + ", cambio=" + cambio
                + ", date=" + date
                + ", idTrabajador=" + idTrabajador
                + ", esDomicilio=" + esDomicilio
                + ", estadoEntrega=" + estadoEntrega
                + '}';
    }

    public static void muestraMovimientos(
            List<MovimientoInventario> movimientos) {

        throw new UnsupportedOperationException(
                "Unimplemented method 'muestraMovimientos'");
    }

    public static void muestra() {

        throw new UnsupportedOperationException(
                "Unimplemented method 'muestra'");
    }

    public static void setControl(
            ControlHistorialMovimientos
                    controlHistorialMovimientos) {

        throw new UnsupportedOperationException(
                "Unimplemented method 'setControl'");
    }

    public int getPago() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPago'");
    }
}