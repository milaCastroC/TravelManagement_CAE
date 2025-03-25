package com.TravelManagement.persistence.mapper;

import com.TravelManagement.domain.dto.VehiculoDTO;
import com.TravelManagement.persistence.entity.Vehiculo;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehiculoMapper {

    @Mapping(source = "vehiculoId", target = "vehiculoId")
    @Mapping(source = "placa", target = "placa")
    @Mapping(source = "capacidad", target = "capacidad")
    @Mapping(source = "tipo", target = "tipo")
    VehiculoDTO toVehiculoDTO(Vehiculo vehiculo);

    // Mapeo inverso de VehiculoDTO a Vehiculo
    @InheritInverseConfiguration
    @Mapping(target = "viajes", ignore = true) // Ignoramos el mapeo de viajes para evitar recursividad
    Vehiculo toVehiculo(VehiculoDTO vehiculoDTO);

}
