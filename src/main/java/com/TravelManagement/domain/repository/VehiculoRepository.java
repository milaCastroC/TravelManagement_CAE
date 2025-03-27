package com.TravelManagement.domain.repository;

import com.TravelManagement.domain.dto.VehiculoDTO;

import java.util.Optional;

public interface VehiculoRepository {

    //Guardar un nuevo vehículo
    VehiculoDTO save(VehiculoDTO vehiculoDTO);

    //Buscar un vehiculo por placa
    Optional<VehiculoDTO> findByPlaca(String placa);

    //Buscar vehículo por id
    Optional<VehiculoDTO> findById(Long vehiculoId);

    //Buscar todos los vehículos
    Iterable<VehiculoDTO> findAll();

    //Editar un vehículo
    VehiculoDTO update(VehiculoDTO vehiculoDTO);

    //Eliminar un vehículo
    void deleteByPlaca(String placa);
}
