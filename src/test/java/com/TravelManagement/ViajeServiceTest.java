package com.TravelManagement;

import com.TravelManagement.domain.dto.VehiculoDTO;
import com.TravelManagement.domain.dto.ViajeDTO;
import com.TravelManagement.domain.repository.VehiculoRepository;
import com.TravelManagement.domain.repository.ViajeRepository;
import com.TravelManagement.domain.service.ViajeService;
import com.TravelManagement.persistence.mapper.ViajeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ViajeServiceTest {
    @Mock
    private ViajeRepository viajeRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private ViajeMapper viajeMapper;

    @InjectMocks
    private ViajeService viajeService;

    private ViajeDTO viajeValido;
    private VehiculoDTO vehiculoValido;
    private LocalDateTime ahora;
    private LocalDateTime manana;
    private LocalDateTime pasadoManana;

    @BeforeEach
    void setUp() {
        ahora = LocalDateTime.now();
        manana = ahora.plusDays(1);
        pasadoManana = ahora.plusDays(2);

        vehiculoValido = new VehiculoDTO(1L, "ABC123", 10, "Bus");
        viajeValido = new ViajeDTO(1L, 1L, "Ciudad A", "Ciudad B",
                manana, pasadoManana, 50.0);
    }

    // Pruebas para registrarViaje()
    @Test
    public void testRegistrarViaje_Valido_ReturnsViajeDTO() {
        // Arrange
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculoValido));
        when(viajeRepository.existeViajeSolapado(anyLong(), any(), any())).thenReturn(false);
        when(viajeRepository.save(viajeValido)).thenReturn(viajeValido);

        // Act
        ViajeDTO result = viajeService.registrarViaje(viajeValido);

        // Assert
        assertNotNull(result);
        assertEquals(viajeValido, result);
        verify(viajeRepository, times(1)).save(viajeValido);
    }

    @Test
    public void testRegistrarViaje_FechaSalidaPasado_ThrowsException() {
        // Arrange
        ViajeDTO viajeInvalido = new ViajeDTO(null, 1L, "A", "B",
                ahora.minusHours(1), manana, 50.0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> viajeService.registrarViaje(viajeInvalido),
                "Debería lanzar excepción cuando fecha salida es en el pasado");
    }

    @Test
    public void testRegistrarViaje_FechaLlegadaAntesSalida_ThrowsException() {
        // Arrange
        ViajeDTO viajeInvalido = new ViajeDTO(null, 1L, "A", "B",
                manana, manana.minusHours(1), 50.0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> viajeService.registrarViaje(viajeInvalido),
                "Debería lanzar excepción cuando llegada es antes de salida");
    }

    @Test
    public void testRegistrarViaje_OrigenDestinoIgual_ThrowsException() {
        // Arrange
        ViajeDTO viajeInvalido = new ViajeDTO(null, 1L, "Ciudad", "Ciudad",
                manana, pasadoManana, 50.0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> viajeService.registrarViaje(viajeInvalido),
                "Debería lanzar excepción cuando origen y destino son iguales");
    }

    @Test
    public void testRegistrarViaje_VehiculoNoExiste_ThrowsException() {
        // Arrange
        when(vehiculoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> viajeService.registrarViaje(viajeValido),
                "Debería lanzar excepción cuando vehículo no existe");
    }

    @Test
    public void testRegistrarViaje_VehiculoConViajeSolapado_ThrowsException() {
        // Arrange
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculoValido));
        when(viajeRepository.existeViajeSolapado(anyLong(), any(), any())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> viajeService.registrarViaje(viajeValido),
                "Debería lanzar excepción cuando vehículo tiene viaje solapado");
    }

    // Pruebas para findAll()
    @Test
    public void testFindAll_ReturnsListOfViajeDTO() {
        // Arrange
        List<ViajeDTO> viajes = Arrays.asList(viajeValido);
        when(viajeRepository.findAll()).thenReturn(viajes);

        // Act
        Iterable<ViajeDTO> result = viajeService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, ((List<ViajeDTO>) result).size());
        assertEquals(viajeValido, ((List<ViajeDTO>) result).get(0));
        verify(viajeRepository, times(1)).findAll();
    }

    // Pruebas para actualizarViaje()
    @Test
    public void testActualizarViaje_Valido_ReturnsViajeDTO() {
        // Arrange
        when(viajeRepository.findById(1L)).thenReturn(Optional.of(viajeValido));
        when(viajeRepository.update(viajeValido)).thenReturn(viajeValido);

        // Act
        ViajeDTO result = viajeService.actualizarViaje(viajeValido);

        // Assert
        assertNotNull(result);
        assertEquals(viajeValido, result);
        verify(viajeRepository, times(1)).update(viajeValido);
    }

    @Test
    public void testActualizarViaje_ViajeNoExiste_ThrowsException() {
        // Arrange
        when(viajeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> viajeService.actualizarViaje(viajeValido),
                "Debería lanzar excepción cuando viaje no existe");
    }

    @Test
    public void testActualizarViaje_ViajeEnCurso_ThrowsException() {
        // Arrange
        ViajeDTO viajeEnCurso = new ViajeDTO(1L, 1L, "A", "B",
                ahora.minusHours(1), manana, 50.0);
        when(viajeRepository.findById(1L)).thenReturn(Optional.of(viajeEnCurso));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> viajeService.actualizarViaje(viajeEnCurso),
                "Debería lanzar excepción cuando viaje está en curso");
    }


    @Test
    public void testActualizarViaje_CambioOrigenConReservas_ThrowsException() {
        // Arrange
        ViajeDTO viajeModificado = new ViajeDTO(1L, 1L, "Nuevo Origen", "B",
                manana, pasadoManana, 50.0);
        when(viajeRepository.findById(1L)).thenReturn(Optional.of(viajeValido));
        when(viajeRepository.tieneReservasActivas(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> viajeService.actualizarViaje(viajeModificado),
                "Debería lanzar excepción cuando se cambia origen con reservas activas");
    }


    // Pruebas para eliminarViaje()
    @Test
    public void testEliminarViaje_Valido_DeletesSuccessfully() {
        // Arrange
        when(viajeRepository.findById(1L)).thenReturn(Optional.of(viajeValido));
        when(viajeRepository.tieneReservasActivas(1L)).thenReturn(false);

        // Act
        viajeService.eliminarViaje(1L);

        // Assert
        verify(viajeRepository, times(1)).delete(1L);
    }

    @Test
    public void testEliminarViaje_ViajeNoExiste_ThrowsException() {
        // Arrange
        when(viajeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> viajeService.eliminarViaje(1L),
                "Debería lanzar excepción cuando viaje no existe");
    }

    @Test
    public void testEliminarViaje_ViajeEnCurso_ThrowsException() {
        // Arrange
        ViajeDTO viajeEnCurso = new ViajeDTO(1L, 1L, "A", "B",
                ahora.minusHours(1), manana, 50.0);
        when(viajeRepository.findById(1L)).thenReturn(Optional.of(viajeEnCurso));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> viajeService.eliminarViaje(1L),
                "Debería lanzar excepción cuando viaje está en curso");
    }

    @Test
    public void testEliminarViaje_ConReservasActivas_ThrowsException() {
        // Arrange
        when(viajeRepository.findById(1L)).thenReturn(Optional.of(viajeValido));
        when(viajeRepository.tieneReservasActivas(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> viajeService.eliminarViaje(1L),
                "Debería lanzar excepción cuando hay reservas activas");
    }
}
