package com.TravelManagement.persistence.mapper;

import com.TravelManagement.domain.dto.ReservaDTO;
import com.TravelManagement.persistence.entity.Cliente;
import com.TravelManagement.persistence.entity.Reserva;
import com.TravelManagement.persistence.entity.Viaje;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    // Mapeo de Reserva a ReservaDTO
    @Mapping(source = "reservaId", target = "reservaId")
    @Mapping(source = "viaje.viajeId", target = "viajeId")
    @Mapping(source = "cliente.clienteId", target = "clienteId")
    @Mapping(source = "estado", target = "estado")
    @Mapping(source = "fechaReserva", target = "fechaReserva")
    ReservaDTO toReservaDto(Reserva reserva);

    // Mapeo inverso de ReservaDTO a Reserva
    @InheritInverseConfiguration
    @Mapping(target = "viaje", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    Reserva toReserva(ReservaDTO reservaDTO);

    // Método de mapeo para convertir Long (viajeId) a Viaje
    @Mapping(target = "viajeId", source = "viaje")
    default Viaje mapViajeId(Long viajeId) {
        if (viajeId != null) {
            Viaje viaje = new Viaje();
            viaje.setViajeId(viajeId);
            return viaje;
        }
        return null;
    }

    //Método de mapeo para convertir Viaje a Long(viajeId)
    @Mapping(target = "viaje", source = "viajeId")
    default Long mapViaje(Viaje viaje) {
        if (viaje != null) {
            return viaje.getViajeId();
        }
        return null;
    }

    // Método de mapeo para convertir Long (clienteId) a Cliente
    @Mapping(target = "clienteId", source = "cliente")
    default Cliente mapClienteId(Long clienteId) {
        if (clienteId != null) {
            Cliente cliente = new Cliente();
            cliente.setClienteId(clienteId);
            return cliente;
        }
        return null;
    }

    //Método de mapeo para convertir Cliente a Long(clienteId)
    default Long mapCliente(Cliente cliente) {
        if (cliente != null) {
            return cliente.getClienteId();
        }
        return null;
    }
}
