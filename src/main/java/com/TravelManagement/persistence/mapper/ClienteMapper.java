package com.TravelManagement.persistence.mapper;

import com.TravelManagement.domain.dto.ClienteDTO;
import com.TravelManagement.persistence.entity.Cliente;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    // Mapeo de Cliente a ClienteDTO
    @Mapping(source = "clienteId", target = "clienteId")
    @Mapping(source = "identificacion", target = "identificacion")
    @Mapping(source = "nombre", target = "nombre")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "telefono", target = "telefono")
    ClienteDTO toClienteDTO(Cliente cliente);

    // Mapeo inverso de ClienteDTO a Cliente
    @InheritInverseConfiguration
    @Mapping(target = "reservas", ignore = true)  // Ignoramos el mapeo de reservas para evitar recursividad
    Cliente toCliente(ClienteDTO clienteDTO);
}
