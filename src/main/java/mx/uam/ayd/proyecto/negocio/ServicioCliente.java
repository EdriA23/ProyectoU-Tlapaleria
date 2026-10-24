package mx.uam.ayd.proyecto.negocio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.ayd.proyecto.datos.ClienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

/**
 * Servicio con la lógica de negocio para registrar y buscar clientes (HU-12).
 */
@Service
public class ServicioCliente {

    public static final String MSG_CAMPOS_OBLIGATORIOS = "Debe llenar los campos obligatorios: Nombre y Teléfono";
    public static final String MSG_CLIENTE_DUPLICADO = "El cliente ya se encuentra registrado en el programa de fidelidad";

    private static final Logger log = LoggerFactory.getLogger(ServicioCliente.class);

    private final ClienteRepository clienteRepository;

    @Autowired
    public ServicioCliente(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Registra un cliente nuevo en el programa de fidelidad.
     * 1. Valida que nombre y teléfono no estén vacíos.
     * 2. RN-01: valida que el teléfono no esté registrado previamente.
     * 3. RN-02: asigna el descuento de fidelidad inicial (5%).
     * 4. Guarda el cliente; la BD le asigna un número de cliente único.
     *
     * @param nombre nombre completo (obligatorio)
     * @param telefono teléfono (obligatorio, único)
     * @param correoElectronico correo electrónico (opcional)
     * @param direccion dirección (opcional)
     * @return el cliente guardado
     * @throws IllegalArgumentException si faltan campos obligatorios o el teléfono ya existe
     */
    @Transactional
    public Cliente registrarCliente(String nombre, String telefono, String correoElectronico, String direccion) {

        String nombreLimpio = limpia(nombre);
        String telefonoLimpio = limpia(telefono);

        if (nombreLimpio.isEmpty() || telefonoLimpio.isEmpty()) {
            log.warn("Intento de registro de cliente con campos obligatorios vacíos");
            throw new IllegalArgumentException(MSG_CAMPOS_OBLIGATORIOS);
        }

        if (clienteRepository.findByTelefono(telefonoLimpio) != null) {
            log.warn("Intento de registrar un cliente con teléfono duplicado: {}", telefonoLimpio);
            throw new IllegalArgumentException(MSG_CLIENTE_DUPLICADO);
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(nombreLimpio);
        cliente.setTelefono(telefonoLimpio);
        cliente.setCorreoElectronico(limpia(correoElectronico));
        cliente.setDireccion(limpia(direccion));
        cliente.setDescuentoFidelidad(Cliente.DESCUENTO_FIDELIDAD_INICIAL);

        Cliente guardado = clienteRepository.save(cliente);
        log.info("Cliente registrado con número {} y descuento de fidelidad {}%",
                guardado.getIdCliente(), guardado.getDescuentoFidelidad());

        return guardado;
    }

    /**
     * Recupera todos los clientes registrados.
     *
     * @return lista de clientes (vacía si no hay ninguno)
     */
    public List<Cliente> recuperaClientes() {
        List<Cliente> clientes = new ArrayList<>();
        clienteRepository.findAll().forEach(clientes::add);
        return clientes;
    }

    /**
     * Búsqueda de clientes por nombre, teléfono o número de cliente.
     * La búsqueda por nombre y teléfono es parcial y no distingue mayúsculas.
     * Si el texto es numérico, también se busca un cliente con ese número (id).
     *
     * @param criterio texto a buscar
     * @return lista de coincidencias sin repetidos (vacía si el criterio es nulo o vacío)
     */
    public List<Cliente> buscaClientes(String criterio) {
        String texto = limpia(criterio);
        if (texto.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Cliente> resultado = new LinkedHashMap<>();

        for (Cliente c : clienteRepository.findByNombreContainingIgnoreCaseOrTelefonoContaining(texto, texto)) {
            resultado.put(c.getIdCliente(), c);
        }

        try {
            long idCliente = Long.parseLong(texto);
            clienteRepository.findById(idCliente).ifPresent(c -> resultado.put(c.getIdCliente(), c));
        } catch (NumberFormatException e) {
            // El texto no es un número de cliente: solo aplica la búsqueda por nombre o teléfono
        }

        return new ArrayList<>(resultado.values());
    }

    private String limpia(String valor) {
        return valor == null ? "" : valor.trim();
    }
}