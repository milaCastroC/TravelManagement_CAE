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
        if(clienteRepository.findByIdentificacion(clienteDTO.getIdentificacion()) != null){
            return clienteRepository.update(clienteDTO);
        }
        throw new IllegalArgumentException("El cliente no existe");
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
