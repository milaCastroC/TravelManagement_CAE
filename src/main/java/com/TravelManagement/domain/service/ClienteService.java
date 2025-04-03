package com.TravelManagement.domain.service;

import com.TravelManagement.domain.dto.ClienteDTO;
import com.TravelManagement.domain.repository.ClienteRepository;
import com.TravelManagement.persistence.mapper.ClienteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteMapper clienteMapper;

    //Guardar un cliente
    public ClienteDTO save(ClienteDTO clienteDTO) {
        if(clienteRepository.findByIdentificacion(clienteDTO.getIdentificacion()).isEmpty()){
             return clienteRepository.save(clienteDTO);
        }
        throw new IllegalArgumentException("El registro ya existe");
    }

    //Buscar clientes
    public Iterable<ClienteDTO> findAll() {

        return clienteRepository.findAll();
    }

    //Buscar cliente por identificación
    public Optional<ClienteDTO> findByIdentificacion(String identificacion) {
        return clienteRepository.findByIdentificacion(identificacion);
    }

    //Actualizar un cliente
    public ClienteDTO updateCliente(ClienteDTO clienteDTO) {
        if (clienteDTO.getIdentificacion() == null || clienteDTO.getIdentificacion().isEmpty()) {
            throw new IllegalArgumentException("La identificación es requerida para actualizar el cliente");
        }

        // Buscar el cliente existente por identificación
        ClienteDTO clienteExistente = clienteRepository.findByIdentificacion(clienteDTO.getIdentificacion())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con identificación: " + clienteDTO.getIdentificacion()));

        // Actualizar solo los campos permitidos
        clienteExistente.setNombre(clienteDTO.getNombre());
        clienteExistente.setEmail(clienteDTO.getEmail());
        clienteExistente.setTelefono(clienteDTO.getTelefono());

        // Guardar el cliente actualizado
        return clienteRepository.update(clienteExistente);
    }

//    Eliminar cliente
    public boolean deleteCliente(String identificacion) {
        Optional<ClienteDTO> cliente = clienteRepository.findByIdentificacion(identificacion);
        if (!cliente.isEmpty()) {
            clienteRepository.deleteByIdentificacion(identificacion);
            return true;
        }
        return false;
    }
}
