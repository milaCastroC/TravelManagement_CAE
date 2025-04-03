package com.TravelManagement;

import com.TravelManagement.domain.dto.ClienteDTO;
import com.TravelManagement.domain.dto.ReservaDTO;
import com.TravelManagement.domain.dto.ViajeDTO;
import com.TravelManagement.domain.repository.ClienteRepository;
import com.TravelManagement.domain.repository.ReservaRepository;
import com.TravelManagement.domain.repository.ViajeRepository;
import com.TravelManagement.domain.service.ReservaService;
import com.TravelManagement.persistence.entity.EstadoReserva;
import com.TravelManagement.persistence.mapper.ReservaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ViajeRepository viajeRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ReservaMapper reservaMapper;

    @InjectMocks
    private ReservaService reservaService;

    private ReservaDTO reservaValida;
    private ViajeDTO viajeValido;
    private ClienteDTO clienteValido;
    private LocalDateTime ahora;
    private LocalDateTime manana;

    @BeforeEach
    void setUp() {
        ahora = LocalDateTime.now();
        manana = ahora.plusDays(1);

        clienteValido = new ClienteDTO(1L, "12345678", "Juan Pérez", "juan@example.com", "555-1234");
        viajeValido = new ViajeDTO(1L, 1L, "Ciudad A", "Ciudad B", manana, manana.plusHours(5), 50.0);
        viajeValido.setAsientosDisponibles(10);

        reservaValida = new ReservaDTO();
        reservaValida.setReservaId(1L);
        reservaValida.setViajeId(1L);
        reservaValida.setClienteId(1L);
        reservaValida.setEstado(EstadoReserva.pendiente.name());
    }

    // Pruebas para crearReserva()
    @Test
    public void testCrearReserva_Valida_ReturnsReservaDTO() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
        when(viajeRepository.findById(1L)).thenReturn(Optional.of(viajeValido));
        when(reservaRepository.findByViajeIdAndClienteId(1L, 1L)).thenReturn(Optional.empty());
        when(reservaRepository.save(reservaValida)).thenReturn(reservaValida);

        // Act
        ReservaDTO result = reservaService.crearReserva(reservaValida);

        // Assert
        assertNotNull(result);
        assertEquals(EstadoReserva.pendiente.name(), result.getEstado());
        verify(reservaRepository, times(1)).save(reservaValida);
    }

    @Test
    public void testCrearReserva_ClienteNoExiste_ThrowsException() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> reservaService.crearReserva(reservaValida),
                "Debería lanzar excepción cuando cliente no existe");
    }

    @Test
    public void testCrearReserva_ViajeNoExiste_ThrowsException() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
        when(viajeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> reservaService.crearReserva(reservaValida),
                "Debería lanzar excepción cuando viaje no existe");
    }

    @Test
    public void testCrearReserva_ViajeYaInicio_ThrowsException() {
        // Arrange
        ViajeDTO viajePasado = new ViajeDTO(1L, 1L, "A", "B", ahora.minusHours(1), ahora.plusHours(4), 50.0);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
        when(viajeRepository.findById(1L)).thenReturn(Optional.of(viajePasado));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> reservaService.crearReserva(reservaValida),
                "Debería lanzar excepción cuando viaje ya inició");
    }

    @Test
    public void testCrearReserva_SinAsientosDisponibles_ThrowsException() {
        // Arrange
        viajeValido.setAsientosDisponibles(0);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
        when(viajeRepository.findById(1L)).thenReturn(Optional.of(viajeValido));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> reservaService.crearReserva(reservaValida),
                "Debería lanzar excepción cuando no hay asientos disponibles");
    }

    @Test
    public void testCrearReserva_ReservaDuplicada_ThrowsException() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
        when(viajeRepository.findById(1L)).thenReturn(Optional.of(viajeValido));
        when(reservaRepository.findByViajeIdAndClienteId(1L, 1L)).thenReturn(Optional.of(reservaValida));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> reservaService.crearReserva(reservaValida),
                "Debería lanzar excepción cuando reserva ya existe");
    }

    // Pruebas para findAll()
    @Test
    public void testFindAll_ReturnsListOfReservaDTO() {
        // Arrange
        List<ReservaDTO> reservas = List.of(reservaValida);
        when(reservaRepository.findAll()).thenReturn(reservas);

        // Act
        Iterable<ReservaDTO> result = reservaService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, ((List<ReservaDTO>) result).size());
        verify(reservaRepository, times(1)).findAll();
    }

    // Pruebas para findByClienteId()
    @Test
    public void testFindByClienteId_ReturnsListOfReservaDTO() {
        // Arrange
        List<ReservaDTO> reservas = List.of(reservaValida);
        when(reservaRepository.findByClienteId(1L)).thenReturn(reservas);

        // Act
        List<ReservaDTO> result = reservaService.findByClienteId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reservaRepository, times(1)).findByClienteId(1L);
    }

    // Pruebas para actualizarReserva()
    @Test
    public void testActualizarReserva_Valida_ReturnsReservaDTO() {
        // Arrange
        ReservaDTO reservaActualizada = new ReservaDTO();
        reservaActualizada.setReservaId(1L);
        reservaActualizada.setEstado(EstadoReserva.confirmada.name());

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaValida));
        when(viajeRepository.findById(1L)).thenReturn(Optional.of(viajeValido));
        when(reservaRepository.update(reservaActualizada)).thenReturn(reservaActualizada);

        // Act
        ReservaDTO result = reservaService.actualizarReserva(reservaActualizada);

        // Assert
        assertNotNull(result);
        assertEquals(EstadoReserva.confirmada.name(), result.getEstado());
        verify(reservaRepository, times(1)).update(reservaActualizada);
    }

    @Test
    public void testActualizarReserva_NoExiste_ThrowsException() {
        // Arrange
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> reservaService.actualizarReserva(reservaValida),
                "Debería lanzar excepción cuando reserva no existe");
    }

    @Test
    public void testActualizarReserva_ViajeYaInicio_ThrowsException() {
        // Arrange
        ViajeDTO viajePasado = new ViajeDTO(1L, 1L, "A", "B", ahora.minusHours(1), ahora.plusHours(4), 50.0);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaValida));
        when(viajeRepository.findById(1L)).thenReturn(Optional.of(viajePasado));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> reservaService.actualizarReserva(reservaValida),
                "Debería lanzar excepción cuando viaje ya inició");
    }

    // Pruebas para cancelarReserva()
    @Test
    public void testCancelarReserva_Valida_ExecutesSuccessfully() {
        // Arrange
        reservaValida.setEstado(EstadoReserva.pendiente.name());
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaValida));
        when(viajeRepository.findById(1L)).thenReturn(Optional.of(viajeValido));

        // Act
        reservaService.cancelarReserva(1L);

        // Assert
        verify(reservaRepository, times(1)).cancelar(1L);
    }

    @Test
    public void testCancelarReserva_NoExiste_ThrowsException() {
        // Arrange
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> reservaService.cancelarReserva(1L),
                "Debería lanzar excepción cuando reserva no existe");
    }

    @Test
    public void testCancelarReserva_ViajeYaInicio_ThrowsException() {
        // Arrange
        ViajeDTO viajePasado = new ViajeDTO(1L, 1L, "A", "B", ahora.minusHours(1), ahora.plusHours(4), 50.0);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaValida));
        when(viajeRepository.findById(1L)).thenReturn(Optional.of(viajePasado));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> reservaService.cancelarReserva(1L),
                "Debería lanzar excepción cuando viaje ya inició");
    }

    @Test
    public void testCancelarReserva_EstadoNoCancelable_ThrowsException() {
        // Arrange
        reservaValida.setEstado(EstadoReserva.en_curso.name());
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaValida));
        when(viajeRepository.findById(1L)).thenReturn(Optional.of(viajeValido));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> reservaService.cancelarReserva(1L),
                "Debería lanzar excepción cuando estado no es cancelable");
    }

    @Test
    public void testCancelarReserva_YaCancelada_ThrowsException() {
        // Arrange
        reservaValida.setEstado(EstadoReserva.cancelado.name());
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaValida));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> reservaService.cancelarReserva(1L),
                "Debería lanzar excepción cuando reserva ya está cancelada");
    }

    // Pruebas para eliminarReserva()
    @Test
    public void testEliminarReserva_Valida_ExecutesSuccessfully() {
        // Arrange
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaValida));

        // Act
        reservaService.eliminarReserva(1L);

        // Assert
        verify(reservaRepository, times(1)).eliminar(1L);
    }

    // Pruebas para actualizarEstadoReserva()
    @Test
    public void testActualizarEstadoReserva_Valida_ReturnsReservaDTO() {
        // Arrange
        String nuevoEstado = EstadoReserva.confirmada.name();
        ReservaDTO reservaActualizada = new ReservaDTO();
        reservaActualizada.setReservaId(1L);
        reservaActualizada.setEstado(nuevoEstado);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaValida));
        when(reservaRepository.update(any())).thenReturn(reservaActualizada);

        // Act
        ReservaDTO result = reservaService.actualizarEstadoReserva(1L, nuevoEstado);

        // Assert
        assertNotNull(result);
        assertEquals(nuevoEstado, result.getEstado());
        verify(reservaRepository, times(1)).update(any());
    }
}
