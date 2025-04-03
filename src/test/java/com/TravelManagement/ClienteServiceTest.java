package com.TravelManagement;

import com.TravelManagement.domain.dto.ClienteDTO;
import com.TravelManagement.domain.repository.ClienteRepository;
import com.TravelManagement.domain.service.ClienteService;
import com.TravelManagement.persistence.mapper.ClienteMapper;
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
public class ClienteServiceTest {
    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteService clienteService;

    // Prueba para findAll()
    @Test
    public void testFindAll_ReturnsListOfClienteDTO() {
        // Arrange
        ClienteDTO dto1 = new ClienteDTO(1L, "12345678", "Juan Pérez", "juan@example.com", "555-1234");
        ClienteDTO dto2 = new ClienteDTO(2L, "87654321", "María López", "maria@example.com", "555-5678");
        List<ClienteDTO> expectedList = Arrays.asList(dto1, dto2);

        when(clienteRepository.findAll()).thenReturn(expectedList);

        // Act
        Iterable<ClienteDTO> result = clienteService.findAll();

        // Assert
        assertNotNull(result, "El resultado no debe ser nulo");
        List<ClienteDTO> resultList = (List<ClienteDTO>) result;
        assertEquals(expectedList.size(), resultList.size(), "El tamaño de la lista debe coincidir");
        assertEquals(expectedList, resultList, "La lista de ClienteDTO debe ser igual a la esperada");
        verify(clienteRepository, times(1)).findAll();
    }

    // Prueba para findByIdentificacion() cuando el cliente existe
    @Test
    public void testFindByIdentificacion_ExistingId_ReturnsClienteDTO() {
        // Arrange
        String identificacion = "12345678";
        ClienteDTO expectedDTO = new ClienteDTO(1L, identificacion, "Juan Pérez", "juan@example.com", "555-1234");

        when(clienteRepository.findByIdentificacion(identificacion)).thenReturn(Optional.of(expectedDTO));

        // Act
        Optional<ClienteDTO> result = clienteService.findByIdentificacion(identificacion);

        // Assert
        assertTrue(result.isPresent(), "El resultado debería estar presente");
        assertEquals(expectedDTO, result.get(), "El ClienteDTO retornado debe ser igual al esperado");
        verify(clienteRepository, times(1)).findByIdentificacion(identificacion);
    }

    // Prueba para findByIdentificacion() cuando el cliente no existe
    @Test
    public void testFindByIdentificacion_NonExistingId_ReturnsEmptyOptional() {
        // Arrange
        String identificacion = "99999999";
        when(clienteRepository.findByIdentificacion(identificacion)).thenReturn(Optional.empty());

        // Act
        Optional<ClienteDTO> result = clienteService.findByIdentificacion(identificacion);

        // Assert
        assertFalse(result.isPresent(), "El resultado debería estar vacío para una identificación inexistente");
        verify(clienteRepository, times(1)).findByIdentificacion(identificacion);
    }

    // Prueba para save() con un nuevo cliente exitoso
    @Test
    public void testSave_NewCliente_ReturnsSavedClienteDTO() {
        // Arrange
        ClienteDTO inputDTO = new ClienteDTO(null, "12345678", "Nuevo Cliente", "nuevo@example.com", "555-0000");
        ClienteDTO savedDTO = new ClienteDTO(3L, "12345678", "Nuevo Cliente", "nuevo@example.com", "555-0000");

        when(clienteRepository.findByIdentificacion("12345678")).thenReturn(Optional.empty());
        when(clienteRepository.save(inputDTO)).thenReturn(savedDTO);

        // Act
        ClienteDTO result = clienteService.save(inputDTO);

        // Assert
        assertNotNull(result.getClienteId(), "El ID del cliente guardado no debe ser nulo");
        assertEquals(savedDTO, result, "El ClienteDTO retornado debe coincidir con el guardado");
        verify(clienteRepository, times(1)).findByIdentificacion("12345678");
        verify(clienteRepository, times(1)).save(inputDTO);
    }

    // Prueba para save() cuando el cliente ya existe
    @Test
    public void testSave_ExistingCliente_ThrowsException() {
        // Arrange
        ClienteDTO inputDTO = new ClienteDTO(null, "12345678", "Cliente Existente", "existente@example.com", "555-1111");
        ClienteDTO existingDTO = new ClienteDTO(1L, "12345678", "Cliente Existente", "existente@example.com", "555-1111");

        when(clienteRepository.findByIdentificacion("12345678")).thenReturn(Optional.of(existingDTO));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> clienteService.save(inputDTO));
        verify(clienteRepository, never()).save(any());
    }

    // Prueba para updateCliente() exitoso
    @Test
    public void testUpdateCliente_ExistingCliente_ReturnsUpdatedClienteDTO() {
        // Arrange
        ClienteDTO inputDTO = new ClienteDTO(1L, "12345678", "Juan Pérez Actualizado", "juan.actualizado@example.com", "555-9999");
        ClienteDTO updatedDTO = new ClienteDTO(1L, "12345678", "Juan Pérez Actualizado", "juan.actualizado@example.com", "555-9999");

        when(clienteRepository.findByIdentificacion("12345678")).thenReturn(Optional.of(inputDTO));
        when(clienteRepository.update(inputDTO)).thenReturn(updatedDTO);

        // Act
        ClienteDTO result = clienteService.updateCliente(inputDTO);

        // Assert
        assertEquals(updatedDTO, result, "El ClienteDTO actualizado debe ser igual al esperado");
        verify(clienteRepository, times(1)).findByIdentificacion("12345678");
        verify(clienteRepository, times(1)).update(inputDTO);
    }

    // Prueba para updateCliente() cuando el cliente no existe
    @Test
    public void testUpdateCliente_NonExistingCliente_ThrowsException() {
        // Arrange
        ClienteDTO inputDTO = new ClienteDTO(99L, "99999999", "No Existe", "noexiste@example.com", "555-0000");

        when(clienteRepository.findByIdentificacion("99999999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> clienteService.updateCliente(inputDTO));
        verify(clienteRepository, never()).update(any());
    }

    // Prueba para deleteCliente() exitoso
    @Test
    public void testDeleteCliente_ExistingCliente_ReturnsTrue() {
        // Arrange
        String identificacion = "12345678";
        ClienteDTO existingDTO = new ClienteDTO(1L, identificacion, "Juan Pérez", "juan@example.com", "555-1234");

        when(clienteRepository.findByIdentificacion(identificacion)).thenReturn(Optional.of(existingDTO));

        // Act
        boolean result = clienteService.deleteCliente(identificacion);

        // Assert
        assertTrue(result, "El resultado debe ser true cuando se elimina correctamente");
        verify(clienteRepository, times(1)).findByIdentificacion(identificacion);
        verify(clienteRepository, times(1)).deleteByIdentificacion(identificacion);
    }

    // Prueba para deleteCliente() cuando el cliente no existe
    @Test
    public void testDeleteCliente_NonExistingCliente_ReturnsFalse() {
        // Arrange
        String identificacion = "99999999";
        when(clienteRepository.findByIdentificacion(identificacion)).thenReturn(Optional.empty());

        // Act
        boolean result = clienteService.deleteCliente(identificacion);

        // Assert
        assertFalse(result, "El resultado debe ser false cuando el cliente no existe");
        verify(clienteRepository, times(1)).findByIdentificacion(identificacion);
        verify(clienteRepository, never()).deleteByIdentificacion(any());
    }
}
