
package mx.uam.ayd.proyecto.negocio.modelo;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidad que representa un corte de caja de la HU-15.
 * Cada registro corresponde a una operación de corte independiente.
 */
@Entity
public class CorteCaja {

    /** Identificador único generado por la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCorte;

    /** Fecha y hora en que se registra el corte. */
    private LocalDateTime fechaHora;

    /** Identificador numérico del vendedor, si está disponible. */
    private int idVendedor;

    /** Clave propia de esta operación de corte. */
    private String turno;

    /** Nombre del empleado que realiza el corte. */
    private String nombreEmpleado;

    /** Suma de los importes de ventas incluidas en el corte. */
    private double totalVentas;

    /** Importe esperado en caja. */
    private double efectivoEsperado;

    /** Constructor requerido por JPA. */
    public CorteCaja() {
    }

    public Long getIdCorte() {
        return idCorte;
    }

    public void setIdCorte(Long idCorte) {
        this.idCorte = idCorte;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(int idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public double getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(double totalVentas) {
        this.totalVentas = totalVentas;
    }

    public double getEfectivoEsperado() {
        return efectivoEsperado;
    }

    public void setEfectivoEsperado(double efectivoEsperado) {
        this.efectivoEsperado = efectivoEsperado;
    }

}
