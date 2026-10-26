package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.datos.ClienteRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;

/**
 * Pruebas unitarias de ServicioCliente para la HU-12 (registro y búsqueda de clientes).
 */
@ExtendWith(MockitoExtension.class)
class ServicioClienteRegistroTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ServicioCliente servicioCliente;

    @Test
    @DisplayName("Escenario 1: Debería registrar al cliente con el descuento de fidelidad del 5%")
    void testRegistrarClienteExitoso() {
        when(clienteRepository.findByTelefono("5551234567")).thenReturn(new ArrayList<>());
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        Cliente resultado = servicioCliente.registrarCliente("  Juan Pérez  ", "5551234567", "juan@correo.com", "Calle 1");

        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.getNombre());
        assertEquals("5551234567", resultado.getTelefono());
        assertEquals("juan@correo.com", resultado.getCorreoElectronico());
        assertEquals(5.0, resultado.getDescuentoFidelidad().doubleValue(), 0.001);
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debería registrar al cliente aunque correo y dirección vengan vacíos")
    void testRegistrarClienteSinCamposOpcionales() {
        when(clienteRepository.findByTelefono("5551234567")).thenReturn(new ArrayList<>());
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        Cliente resultado = servicioCliente.registrarCliente("Ana", "5551234567", null, null);

        assertNotNull(resultado);
        assertEquals("", resultado.getCorreoElectronico());
        assertEquals("", resultado.getDireccion());
    }

    @Test
    @DisplayName("Escenario 2: Debería rechazar el registro si falta nombre o teléfono")
    void testRegistrarClienteCamposObligatoriosVacios() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> servicioCliente.registrarCliente("", "5551234567", null, null));
        assertEquals(ServicioCliente.MSG_CAMPOS_OBLIGATORIOS, ex1.getMessage());

        assertThrows(IllegalArgumentException.class,
                () -> servicioCliente.registrarCliente("Juan", "   ", null, null));
        assertThrows(IllegalArgumentException.class,
                () -> servicioCliente.registrarCliente(null, null, null, null));

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Escenario 3 (RN-01): Debería rechazar un teléfono ya registrado")
    void testRegistrarClienteTelefonoDuplicado() {
        List<Cliente> existentes = new ArrayList<>();
        existentes.add(new Cliente());
        when(clienteRepository.findByTelefono("5551234567")).thenReturn(existentes);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicioCliente.registrarCliente("Juan", "5551234567", null, null));

        assertEquals(ServicioCliente.MSG_CLIENTE_DUPLICADO, ex.getMessage());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debería recuperar todos los clientes registrados")
    void testRecuperaClientes() {
        List<Cliente> lista = new ArrayList<>();
        lista.add(new Cliente());
        lista.add(new Cliente());
        when(clienteRepository.findAll()).thenReturn(lista);

        assertEquals(2, servicioCliente.recuperaClientes().size());
    }

    @Test
    @DisplayName("Debería buscar clientes por nombre o teléfono")
    void testBuscaClientesPorNombreOTelefono() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setNombre("Juan Pérez");
        List<Cliente> lista = new ArrayList<>();
        lista.add(cliente);
        when(clienteRepository.findByNombreContainingIgnoreCaseOrTelefonoContaining("juan", "juan")).thenReturn(lista);

        List<Cliente> resultado = servicioCliente.buscaClientes(" juan ");

        assertEquals(1, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("Debería buscar un cliente por su número de cliente")
    void testBuscaClientesPorNumeroDeCliente() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(7L);
        cliente.setNombre("Ana López");
        when(clienteRepository.findByNombreContainingIgnoreCaseOrTelefonoContaining("7", "7")).thenReturn(new ArrayList<>());
        when(clienteRepository.findById(7L)).thenReturn(Optional.of(cliente));

        List<Cliente> resultado = servicioCliente.buscaClientes("7");

        assertEquals(1, resultado.size());
        assertEquals("Ana López", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("No debería repetir un cliente que coincide por teléfono y por número")
    void testBuscaClientesSinRepetidos() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(7L);
        List<Cliente> lista = new ArrayList<>();
        lista.add(cliente);
        when(clienteRepository.findByNombreContainingIgnoreCaseOrTelefonoContaining("7", "7")).thenReturn(lista);
        when(clienteRepository.findById(7L)).thenReturn(Optional.of(cliente));

        assertEquals(1, servicioCliente.buscaClientes("7").size());
    }

    @Test
    @DisplayName("Debería regresar lista vacía si el criterio de búsqueda es nulo o vacío")
    void testBuscaClientesCriterioVacio() {
        assertTrue(servicioCliente.buscaClientes(null).isEmpty());
        assertTrue(servicioCliente.buscaClientes("   ").isEmpty());
        verify(clienteRepository, never()).findByNombreContainingIgnoreCaseOrTelefonoContaining(any(), any());
    }
}
