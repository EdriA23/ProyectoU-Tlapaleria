package mx.uam.ayd.proyecto.negocio.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Entidad de negocio Tarjeta.
 */
@Entity
public class Tarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTarjeta;

    private String tipoTarjeta;
    private String numeroTarjeta;
    private String cvv;
    private String fechaVencimiento;

    // Relación ManyToOne hacia el Cliente
    @ManyToOne
    @JoinColumn(name = "id_cliente_fk", nullable = true)
    private Cliente cliente;

    public Tarjeta() {}

    public Tarjeta(String tipoTarjeta, String numeroTarjeta, String cvv, String fechaVencimiento) {
        this.tipoTarjeta = tipoTarjeta;
        this.numeroTarjeta = numeroTarjeta;
        this.cvv = cvv;
        this.fechaVencimiento = fechaVencimiento;
    }

    public Long getIdTarjeta() { return idTarjeta; }
    public void setIdTarjeta(Long idTarjeta) { this.idTarjeta = idTarjeta; }

    public String getTipoTarjeta() { return tipoTarjeta; }
    public void setTipoTarjeta(String tipoTarjeta) { this.tipoTarjeta = tipoTarjeta; }

    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    @Override
    public String toString() {
        String ultimos4 = (numeroTarjeta != null && numeroTarjeta.length() >= 4) 
                ? numeroTarjeta.substring(numeroTarjeta.length() - 4) 
                : "****";
        return String.format("%s - **** **** **** %s (Exp: %s)", 
                tipoTarjeta != null ? tipoTarjeta : "TARJETA", ultimos4, fechaVencimiento);
    }
}