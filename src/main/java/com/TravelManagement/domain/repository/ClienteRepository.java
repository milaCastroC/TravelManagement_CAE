package com.TravelManagement.domain.repository;

import com.TravelManagement.domain.dto.ClienteDTO;

import java.util.Optional;

public interface ClienteRepository {

        // Guardar un nuevo cliente
        ClienteDTO save(ClienteDTO clienteDTO);

        //Buscar cliente por identificacion
        Optional<ClienteDTO> findByIdentificacion(String identificacion);

        //Buscar todos los clientes
        Iterable<ClienteDTO> findAll();

        //Editar un cliente
        ClienteDTO update(ClienteDTO clienteDTO);

        //Eliminar un cliente
        void deleteByIdentificacion(String identificacion);
}
