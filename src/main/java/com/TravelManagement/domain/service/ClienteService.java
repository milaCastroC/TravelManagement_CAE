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
        return clienteRepository.save(clienteDTO);
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
        return clienteRepository.update(clienteDTO);
    }

    //Eliminar cliente
    public boolean deleteCliente(String identificacion) {
        return clienteRepository.deleteByIdentificacion(identificacion);
    }
}
