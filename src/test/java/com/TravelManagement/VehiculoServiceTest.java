package com.TravelManagement;

import com.TravelManagement.domain.dto.VehiculoDTO;
import com.TravelManagement.domain.repository.VehiculoRepository;
import com.TravelManagement.domain.service.VehiculoService;
import com.TravelManagement.persistence.mapper.VehiculoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehiculoServiceTest {
    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private VehiculoMapper vehiculoMapper;

    @InjectMocks
    private VehiculoService vehiculoService;

    // Prueba para save() con un vehículo nuevo exitoso
    @Test
    public void testSave_NewVehiculo_ReturnsSavedVehiculoDTO() {
        // Arrange
        VehiculoDTO inputDTO = new VehiculoDTO(null, "ABC123", 4, "Sedan");
        VehiculoDTO savedDTO = new VehiculoDTO(1L, "ABC123", 4, "Sedan");

        when(vehiculoRepository.findByPlaca("ABC123")).thenReturn(Optional.empty());
        when(vehiculoRepository.save(inputDTO)).thenReturn(savedDTO);

        // Act
        VehiculoDTO result = vehiculoService.save(inputDTO);

        // Assert
        assertNotNull(result.getVehiculoId(), "El ID del vehículo guardado no debe ser nulo");
        assertEquals(savedDTO, result, "El VehiculoDTO retornado debe coincidir con el guardado");
        verify(vehiculoRepository, times(1)).findByPlaca("ABC123");
        verify(vehiculoRepository, times(1)).save(inputDTO);
    }

    // Prueba para save() cuando el vehículo ya existe
    @Test
    public void testSave_ExistingVehiculo_ThrowsException() {
        // Arrange
        VehiculoDTO inputDTO = new VehiculoDTO(null, "EXIST123", 5, "SUV");
        VehiculoDTO existingDTO = new VehiculoDTO(2L, "EXIST123", 5, "SUV");

        when(vehiculoRepository.findByPlaca("EXIST123")).thenReturn(Optional.of(existingDTO));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vehiculoService.save(inputDTO),
                "Debería lanzar IllegalArgumentException cuando el vehículo ya existe"
        );

        assertEquals("El vehículo ya existe", exception.getMessage());
        verify(vehiculoRepository, never()).save(any());
    }

    // Prueba para findAll()
    @Test
    public void testFindAll_ReturnsListOfVehiculoDTO() {
        // Arrange
        VehiculoDTO dto1 = new VehiculoDTO(1L, "ABC123", 4, "Sedan");
        VehiculoDTO dto2 = new VehiculoDTO(2L, "XYZ789", 8, "Van");
        List<VehiculoDTO> expectedList = Arrays.asList(dto1, dto2);

        when(vehiculoRepository.findAll()).thenReturn(expectedList);

        // Act
        Iterable<VehiculoDTO> result = vehiculoService.findAll();

        // Assert
        assertNotNull(result, "El resultado no debe ser nulo");
        List<VehiculoDTO> resultList = (List<VehiculoDTO>) result;
        assertEquals(expectedList.size(), resultList.size(), "El tamaño de la lista debe coincidir");
        assertEquals(expectedList, resultList, "La lista de VehiculoDTO debe ser igual a la esperada");
        verify(vehiculoRepository, times(1)).findAll();
    }

    // Prueba para findByPlaca() cuando el vehículo existe
    @Test
    public void testFindByPlaca_ExistingPlaca_ReturnsVehiculoDTO() {
        // Arrange
        String placa = "ABC123";
        VehiculoDTO expectedDTO = new VehiculoDTO(1L, placa, 4, "Sedan");

        when(vehiculoRepository.findByPlaca(placa)).thenReturn(Optional.of(expectedDTO));

        // Act
        Optional<VehiculoDTO> result = vehiculoService.findByPlaca(placa);

        // Assert
        assertTrue(result.isPresent(), "El resultado debería estar presente");
        assertEquals(expectedDTO, result.get(), "El VehiculoDTO retornado debe ser igual al esperado");
        verify(vehiculoRepository, times(1)).findByPlaca(placa);
    }

    // Prueba para findByPlaca() cuando el vehículo no existe
    @Test
    public void testFindByPlaca_NonExistingPlaca_ReturnsEmptyOptional() {
        // Arrange
        String placa = "NOEXIST";
        when(vehiculoRepository.findByPlaca(placa)).thenReturn(Optional.empty());

        // Act
        Optional<VehiculoDTO> result = vehiculoService.findByPlaca(placa);

        // Assert
        assertFalse(result.isPresent(), "El resultado debería estar vacío para una placa inexistente");
        verify(vehiculoRepository, times(1)).findByPlaca(placa);
    }


    // Prueba para updateVehiculo() exitoso
    @Test
    public void testUpdateVehiculo_ExistingVehiculo_ReturnsUpdatedVehiculoDTO() {
        // Arrange
        VehiculoDTO inputDTO = new VehiculoDTO(1L, "ABC123", 5, "Sedan Modificado");
        VehiculoDTO existingDTO = new VehiculoDTO(1L, "ABC123", 4, "Sedan");
        VehiculoDTO updatedDTO = new VehiculoDTO(1L, "ABC123", 5, "Sedan Modificado");

        when(vehiculoRepository.findByPlaca("ABC123")).thenReturn(Optional.of(existingDTO));
        when(vehiculoRepository.update(inputDTO)).thenReturn(updatedDTO);

        // Act
        VehiculoDTO result = vehiculoService.updateVehiculo(inputDTO);

        // Assert
        assertEquals(updatedDTO, result, "El VehiculoDTO actualizado debe ser igual al esperado");
        verify(vehiculoRepository, times(1)).findByPlaca("ABC123");
        verify(vehiculoRepository, times(1)).update(inputDTO);
    }

    // Prueba para updateVehiculo() cuando el vehículo no existe
    @Test
    public void testUpdateVehiculo_NonExistingVehiculo_ThrowsException() {
        // Arrange
        VehiculoDTO inputDTO = new VehiculoDTO(99L, "NOEXIST", 4, "Fantasma");

        when(vehiculoRepository.findByPlaca("NOEXIST")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vehiculoService.updateVehiculo(inputDTO),
                "Debería lanzar IllegalArgumentException cuando el vehículo no existe"
        );

        assertEquals("El vehiculo no existe", exception.getMessage());
        verify(vehiculoRepository, never()).update(any());
    }

    // Prueba para deleteVehiculo() exitoso
    @Test
    public void testDeleteVehiculo_ExistingVehiculo_ReturnsTrue() {
        // Arrange
        String placa = "ABC123";
        VehiculoDTO existingDTO = new VehiculoDTO(1L, placa, 4, "Sedan");

        when(vehiculoRepository.findByPlaca(placa)).thenReturn(Optional.of(existingDTO));

        // Act
        boolean result = vehiculoService.deleteVehiculo(placa);

        // Assert
        assertTrue(result, "El resultado debe ser true cuando se elimina correctamente");
        verify(vehiculoRepository, times(1)).findByPlaca(placa);
        verify(vehiculoRepository, times(1)).deleteByPlaca(placa);
    }

    // Prueba para deleteVehiculo() cuando el vehículo no existe
    @Test
    public void testDeleteVehiculo_NonExistingVehiculo_ReturnsFalse() {
        // Arrange
        String placa = "NOEXIST";
        when(vehiculoRepository.findByPlaca(placa)).thenReturn(Optional.empty());

        // Act
        boolean result = vehiculoService.deleteVehiculo(placa);

        // Assert
        assertFalse(result, "El resultado debe ser false cuando el vehículo no existe");
        verify(vehiculoRepository, times(1)).findByPlaca(placa);
        verify(vehiculoRepository, never()).deleteByPlaca(any());
    }
}
