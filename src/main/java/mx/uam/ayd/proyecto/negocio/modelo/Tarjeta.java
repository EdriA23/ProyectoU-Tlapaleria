package mx.uam.ayd.proyecto.negocio.modelo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Entidad de negocio que representa una Tarjeta de pago dentro del sistema.
 * Mapea los datos del metodo de pago y gestiona la relacion bidireccional
 * con la entidad Cliente (1 a N).
 */
@Entity
public class Tarjeta {

    /** Identificador unico de la tarjeta generado automaticamente. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTarjeta;

    /** Tipo de tarjeta (ej. CREDITO, DEBITO). */
    private String tipoTarjeta;

    /** Numero de tarjeta registrado para la transaccion. */
    private String numeroTarjeta;

    /** Codigo de verificacion (CVV) de la tarjeta. */
    private String cvv;

    /** Fecha de vencimiento en formato MM/YY. */
    private String fechaVencimiento;

    /**
     * Lista de clientes asociados a esta tarjeta (Relacion 1 a N).
     * Mapeado por el atributo 'tarjeta' en la entidad Cliente.
     */
    @OneToMany(mappedBy = "tarjeta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cliente> clientes = new ArrayList<>();

    /**
     * Constructor por defecto requerido obligatoriamente por JPA/Hibernate.
     */
    public Tarjeta() {
    }

    /**
     * Constructor parametrizado para instanciar una tarjeta con sus datos base.
     *
     * @param tipoTarjeta tipo de tarjeta (CREDITO, DEBITO, etc.)
     * @param numeroTarjeta numero de la tarjeta
     * @param cvv codigo de seguridad
     * @param fechaVencimiento fecha de expiracion (MM/YY)
     */
    public Tarjeta(String tipoTarjeta, String numeroTarjeta, String cvv, String fechaVencimiento) {
        this.tipoTarjeta = tipoTarjeta;
        this.numeroTarjeta = numeroTarjeta;
        this.cvv = cvv;
        this.fechaVencimiento = fechaVencimiento;
    }

    /**
     * Agrega un cliente a la lista de clientes asociados y actualiza
     * la referencia bidireccional en el objeto Cliente.
     *
     * @param cliente el cliente a vincular con esta tarjeta
     */
    public void agregarCliente(Cliente cliente) {
        clientes.add(cliente);
        cliente.setTarjeta(this);
    }

    /**
     * Remueve un cliente de la lista de clientes asociados y remueve
     * la referencia de esta tarjeta en el objeto Cliente.
     *
     * @param cliente el cliente a desvincular de esta tarjeta
     */
    public void removerCliente(Cliente cliente) {
        clientes.remove(cliente);
        cliente.setTarjeta(null);
    }

    /**
     * Obtiene el ID unico de la tarjeta.
     *
     * @return idTarjeta
     */
    public Long getIdTarjeta() {
        return idTarjeta;
    }

    /**
     * Establece el ID unico de la tarjeta.
     *
     * @param idTarjeta nuevo id de la tarjeta
     */
    public void setIdTarjeta(Long idTarjeta) {
        this.idTarjeta = idTarjeta;
    }

    /**
     * Obtiene el tipo de tarjeta.
     *
     * @return tipoTarjeta
     */
    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    /**
     * Establece el tipo de tarjeta.
     *
     * @param tipoTarjeta tipo de tarjeta
     */
    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    /**
     * Obtiene el numero de tarjeta.
     *
     * @return numeroTarjeta
     */
    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    /**
     * Establece el numero de tarjeta.
     *
     * @param numeroTarjeta numero de la tarjeta
     */
    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    /**
     * Obtiene el codigo CVV.
     *
     * @return cvv
     */
    public String getCvv() {
        return cvv;
    }

    /**
     * Establece el codigo CVV.
     *
     * @param cvv codigo de seguridad
     */
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    /**
     * Obtiene la fecha de vencimiento.
     *
     * @return fechaVencimiento
     */
    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    /**
     * Establece la fecha de vencimiento.
     *
     * @param fechaVencimiento fecha en formato MM/YY
     */
    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /**
     * Obtiene la lista de clientes vinculados a esta tarjeta.
     *
     * @return lista de objetos Cliente
     */
    public List<Cliente> getClientes() {
        return clientes;
    }

    /**
     * Establece la lista de clientes vinculados a esta tarjeta.
     *
     * @param clientes lista de objetos Cliente
     */
    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }
}