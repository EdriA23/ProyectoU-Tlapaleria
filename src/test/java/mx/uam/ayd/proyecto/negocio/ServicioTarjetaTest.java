package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.datos.ClienteRepository;
import mx.uam.ayd.proyecto.datos.TarjetaRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Cliente;
import mx.uam.ayd.proyecto.negocio.modelo.Tarjeta;

@ExtendWith(MockitoExtension.class)
public class ServicioTarjetaTest {

    @Mock
    private TarjetaRepository tarjetaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ServicioTarjeta servicioTarjeta;

    private Cliente clientePrueba;
    private Tarjeta tarjetaValida;

    @BeforeEach
    void setUp() {
        clientePrueba = new Cliente();
        clientePrueba.setIdCliente(1L);
        clientePrueba.setNombre("Juan Pérez");

        tarjetaValida = new Tarjeta("CREDITO", "1234567890123456", "123", "12/28");
    }

    // ==========================================
    // PRUEBAS DE obtenerTarjetasPorCliente
    // ==========================================

    @Test
    @DisplayName("obtenerTarjetasPorCliente - Retorna lista de tarjetas cuando el cliente es válido")
    void testObtenerTarjetasPorClienteExito() {
        when(tarjetaRepository.findByCliente(clientePrueba))
                .thenReturn(Arrays.asList(tarjetaValida));

        List<Tarjeta> resultado = servicioTarjeta.obtenerTarjetasPorCliente(clientePrueba);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(tarjetaRepository).findByCliente(clientePrueba);
    }

    @Test
    @DisplayName("obtenerTarjetasPorCliente - Retorna lista vacía si el cliente es null")
    void testObtenerTarjetasPorClienteNull() {
        List<Tarjeta> resultado = servicioTarjeta.obtenerTarjetasPorCliente(null);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(tarjetaRepository, never()).findByCliente(any());
    }

    // ==========================================
    // PRUEBAS DE validarDatosTarjeta
    // ==========================================

    @Test
    @DisplayName("validarDatosTarjeta - Retorna true con una tarjeta totalmente válida")
    void testValidarDatosTarjetaValida() {
        assertTrue(servicioTarjeta.validarDatosTarjeta(tarjetaValida));
    }

    @Test
    @DisplayName("validarDatosTarjeta - Retorna false con tarjeta nula o campos vacíos/inválidos")
    void testValidarDatosTarjetaInvalidos() {
        assertFalse(servicioTarjeta.validarDatosTarjeta(null));

        // Tipo nulo o vacío
        assertFalse(servicioTarjeta.validarDatosTarjeta(new Tarjeta("", "1234567890123456", "123", "12/28")));

        // Número con longitud distinta a 16 dígitos
        assertFalse(servicioTarjeta.validarDatosTarjeta(new Tarjeta("CREDITO", "12345", "123", "12/28")));

        // CVV con longitud menor a 3 o mayor a 4
        assertFalse(servicioTarjeta.validarDatosTarjeta(new Tarjeta("CREDITO", "1234567890123456", "12", "12/28")));
        assertFalse(servicioTarjeta.validarDatosTarjeta(new Tarjeta("CREDITO", "1234567890123456", "12345", "12/28")));

        // Formato de vencimiento incorrecto (debe ser MM/YY)
        assertFalse(servicioTarjeta.validarDatosTarjeta(new Tarjeta("CREDITO", "1234567890123456", "123", "13/28"))); // Mes 13 inválido
        assertFalse(servicioTarjeta.validarDatosTarjeta(new Tarjeta("CREDITO", "1234567890123456", "123", "12-28"))); // Formato con guion
    }

    // ==========================================
    // PRUEBAS DE guardarTarjetaParaCliente
    // ==========================================

    @Test
    @DisplayName("guardarTarjetaParaCliente - Guarda con éxito asociada a un cliente")
    void testGuardarTarjetaParaClienteExito() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clientePrueba));
        when(tarjetaRepository.save(any(Tarjeta.class))).thenReturn(tarjetaValida);

        Tarjeta resultado = servicioTarjeta.guardarTarjetaParaCliente(tarjetaValida, 1L);

        assertNotNull(resultado);
        assertEquals(clientePrueba, tarjetaValida.getCliente());
        verify(clienteRepository).findById(1L);
        verify(tarjetaRepository).save(tarjetaValida);
    }

    @Test
    @DisplayName("guardarTarjetaParaCliente - Guarda con éxito para Público General (idCliente = null)")
    void testGuardarTarjetaParaPublicoGeneralExito() {
        when(tarjetaRepository.save(any(Tarjeta.class))).thenReturn(tarjetaValida);

        Tarjeta resultado = servicioTarjeta.guardarTarjetaParaCliente(tarjetaValida, null);

        assertNotNull(resultado);
        assertNull(tarjetaValida.getCliente());
        verify(clienteRepository, never()).findById(any());
        verify(tarjetaRepository).save(tarjetaValida);
    }

    @Test
    @DisplayName("guardarTarjetaParaCliente - Lanza excepción si la tarjeta no es válida")
    void testGuardarTarjetaInvalidaLanzaExcepcion() {
        Tarjeta tarjetaInvalida = new Tarjeta("CREDITO", "123", "12", "99/99");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioTarjeta.guardarTarjetaParaCliente(tarjetaInvalida, 1L);
        });

        assertEquals("Los datos de la tarjeta son invalidos o estan incompletos.", ex.getMessage());
        verify(tarjetaRepository, never()).save(any());
    }

    @Test
    @DisplayName("guardarTarjetaParaCliente - Lanza excepción si el idCliente no existe en BD")
    void testGuardarTarjetaClienteNoEncontradoLanzaExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicioTarjeta.guardarTarjetaParaCliente(tarjetaValida, 99L);
        });

        assertEquals("El cliente especificado no existe.", ex.getMessage());
        verify(tarjetaRepository, never()).save(any());
    }

    // ==========================================
    // PRUEBAS DE BÚSQUEDA (buscarPorId y buscarPorNumeroTarjeta)
    // ==========================================

    @Test
    @DisplayName("buscarPorId - Retorna tarjeta si existe o null si id es nulo/inexistente")
    void testBuscarPorId() {
        when(tarjetaRepository.findById(1L)).thenReturn(Optional.of(tarjetaValida));

        assertEquals(tarjetaValida, servicioTarjeta.buscarPorId(1L));
        assertNull(servicioTarjeta.buscarPorId(null));
        
        when(tarjetaRepository.findById(2L)).thenReturn(Optional.empty());
        assertNull(servicioTarjeta.buscarPorId(2L));
    }

    @Test
    @DisplayName("buscarPorNumeroTarjeta - Retorna tarjeta si el número coincide")
    void testBuscarPorNumeroTarjeta() {
        when(tarjetaRepository.findByNumeroTarjeta("1234567890123456")).thenReturn(tarjetaValida);

        assertEquals(tarjetaValida, servicioTarjeta.buscarPorNumeroTarjeta("1234567890123456"));
        assertNull(servicioTarjeta.buscarPorNumeroTarjeta(null));
        assertNull(servicioTarjeta.buscarPorNumeroTarjeta("   "));
    }
}