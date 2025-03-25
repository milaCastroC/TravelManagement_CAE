package com.TravelManagement.persistence.mapper;

import com.TravelManagement.domain.dto.ViajeDTO;
import com.TravelManagement.persistence.entity.Vehiculo;
import com.TravelManagement.persistence.entity.Viaje;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ViajeMapper {
    // Mapeo de Viaje a ViajeDTO
    @Mapping(source = "viajeId", target = "viajeId")
    @Mapping(source = "vehiculoId", target = "vehiculoId")
    @Mapping(source = "origen", target = "origen")
    @Mapping(source = "destino", target = "destino")
    @Mapping(source = "fechaSalida", target = "fechaSalida")
    @Mapping(source = "fechaLlegada", target = "fechaLlegada")
    @Mapping(source = "precio", target = "precio")
    ViajeDTO toViajeDto(Viaje viaje);

    // Mapeo inverso de ViajeDTO a Viaje
    @InheritInverseConfiguration
    @Mapping(target = "vehiculo", source = "vehiculoId")
    @Mapping(target = "reservas", ignore = true) // Ignoramos el mapeo de reservas para evitar recursividad
    Viaje toViaje(ViajeDTO viajeDTO);

    //Método de mapeo para convertir Vehiculo a Long(VehiculoId)
    @Mapping(target = "vehiculoId", source = "vehiculo")
    default Long mapVehiculo(Vehiculo vehiculo) {
        if(vehiculo != null){
            return vehiculo.getVehiculoId();
        }
        return null;
    }

    // Método de mapeo para convertir Long (vehiculoId) a Vehiculo
    @Mapping(target = "vehiculo", source = "vehiculoId" )
    default Vehiculo mapVehiculoId(Long vehiculoId) {
        if (vehiculoId != null) {
            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setVehiculoId(vehiculoId);
            return vehiculo;
        }
        return null;
    }
}
