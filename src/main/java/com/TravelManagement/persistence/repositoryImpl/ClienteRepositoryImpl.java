package com.TravelManagement.persistence.repositoryImpl;

import com.TravelManagement.domain.dto.ClienteDTO;
import com.TravelManagement.domain.repository.ClienteRepository;
import com.TravelManagement.persistence.crud.ClienteCrudRepository;
import com.TravelManagement.persistence.entity.Cliente;
import com.TravelManagement.persistence.mapper.ClienteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ClienteRepositoryImpl implements ClienteRepository {

    @Autowired
    private ClienteCrudRepository clienteCrudRepository;

    @Autowired
    private ClienteMapper clienteMapper;

    // Guardar un nuevo cliente
    @Override
    public ClienteDTO save(ClienteDTO clienteDTO) {
        Cliente cliente = clienteMapper.toCliente(clienteDTO);
        if(findByIdentificacion(clienteDTO.getIdentificacion()) != null){
            Cliente savedCliente = clienteCrudRepository.save(cliente);
            return clienteMapper.toClienteDTO(savedCliente);
        }
        throw new IllegalArgumentException("El registro ya existe");
    }

    //Buscar cliente por identificacion
    @Override
    public Optional<ClienteDTO> findByIdentificacion(String identificacion) {
        Optional<Cliente> cliente = clienteCrudRepository.findByIdentificacion(identificacion);
        return cliente.map(clienteMapper::toClienteDTO);
    }

    //Consultar todos los clientes
    @Override
    public Iterable<ClienteDTO> findAll() {
        Iterable<Cliente> clientes = clienteCrudRepository.findAll();
        return ((List<Cliente>) clientes).stream()
                .map(clienteMapper::toClienteDTO)
                .collect(Collectors.toList());
    }

    //Editar un cliente
    @Override
    public ClienteDTO update(ClienteDTO clienteDTO) {
        if (clienteDTO.getIdentificacion() == null || clienteDTO.getIdentificacion().isEmpty()) {
            throw new IllegalArgumentException("La identificación es requerida para actualizar el cliente");
        }
        // Buscar el cliente existente por identificación
        Cliente clienteExistente = clienteCrudRepository.findByIdentificacion(clienteDTO.getIdentificacion())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con identificación: " + clienteDTO.getIdentificacion()));

        // Actualizar solo los campos permitidos
        clienteExistente.setNombre(clienteDTO.getNombre());
        clienteExistente.setEmail(clienteDTO.getEmail());
        clienteExistente.setTelefono(clienteDTO.getTelefono());

        // Guardar el cliente actualizado
        Cliente updatedCliente = clienteCrudRepository.save(clienteExistente);

        // Convertir y retornar el cliente actualizado como DTO
        return clienteMapper.toClienteDTO(updatedCliente);
    }

    @Override
    public void deleteByIdentificacion(String identificacion) {
        // Verificar si el cliente existe
        Cliente cliente = clienteCrudRepository.findByIdentificacion(identificacion)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con identificación: " + identificacion));

        // Verificar si el cliente tiene reservas activas con una consulta a la BD
        boolean tieneReservasActivas = clienteCrudRepository.existsClienteByIdentificacionAndReservasEstado(identificacion, "ACTIVA");

        if (tieneReservasActivas) {
            throw new IllegalStateException("No se puede eliminar el cliente, tiene reservas activas.");
        }

        // Si no tiene reservas activas, eliminar el cliente
        clienteCrudRepository.delete(cliente);
    }
}

