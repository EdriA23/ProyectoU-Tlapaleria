package mx.uam.ayd.proyecto.negocio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mx.uam.ayd.proyecto.datos.TrabajadorRepository;
import mx.uam.ayd.proyecto.negocio.modelo.Trabajador;

@ExtendWith(MockitoExtension.class)
public class ServicioTrabajadorTest {

    @Mock
    private TrabajadorRepository repositoryTrabajador;

    @InjectMocks
    private ServicioTrabajador servicioTrabajador;

    private Trabajador trabajador;

    @BeforeEach
    void setUp() {
        trabajador = new Trabajador("Juan Pérez", "5512345678", "Vendedor", "juan@gmail.com", "EMP-001");
    }

    @Test
    void testCreaTrabajador() {
        when(repositoryTrabajador.save(any(Trabajador.class))).thenReturn(trabajador);

        Trabajador resultado = servicioTrabajador.creaTrabajador("EMP-001", "Juan Pérez", "5512345678", "Vendedor", "juan@gmail.com");

        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.getNombreCompleto());
    }

    @Test
    void testBuscarTrabajadores() {
        when(repositoryTrabajador.findAll()).thenReturn(Arrays.asList(trabajador));

        List<Trabajador> resultados = servicioTrabajador.buscarTrabajadores("juan");

        assertEquals(1, resultados.size());
        assertEquals("Juan Pérez", resultados.get(0).getNombreCompleto());
    }

    @Test
    void testEliminarTrabajador() {
        when(repositoryTrabajador.existsById(1L)).thenReturn(true);

        boolean eliminado = servicioTrabajador.eliminarTrabajador(1L);

        assertTrue(eliminado);
        verify(repositoryTrabajador).deleteById(1L);
    }
    @Test
    void testObtenerDetallesExiste() {
        when(repositoryTrabajador.findById(1L)).thenReturn(java.util.Optional.of(trabajador));

        Trabajador resultado = servicioTrabajador.obtenerDetalles(1L);

        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.getNombreCompleto());
    }

    @Test
    void testEliminarTrabajadorNoExiste() {
        when(repositoryTrabajador.existsById(99L)).thenReturn(false);

        boolean eliminado = servicioTrabajador.eliminarTrabajador(99L);

        assertFalse(eliminado);
        verify(repositoryTrabajador, never()).deleteById(anyLong());
    }
}