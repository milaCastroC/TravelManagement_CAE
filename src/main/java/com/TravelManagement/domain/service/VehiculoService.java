package com.TravelManagement.domain.service;

import com.TravelManagement.domain.dto.VehiculoDTO;
import com.TravelManagement.domain.repository.VehiculoRepository;
import com.TravelManagement.persistence.mapper.VehiculoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private VehiculoMapper vehiculoMapper;

    //Guardar un vehiculo
    public VehiculoDTO save(VehiculoDTO vehiculoDTO){
        if(vehiculoRepository.findByPlaca(vehiculoDTO.getPlaca()).isEmpty()){
            return vehiculoRepository.save(vehiculoDTO);
        }
        throw new IllegalArgumentException("El vehículo ya existe");
    }

    //Buscar vehiculos
    public Iterable<VehiculoDTO> findAll(){
        return vehiculoRepository.findAll();
    }

    //Buscar vehiculo por placa
    public Optional<VehiculoDTO> findByPlaca(String placa){
        return vehiculoRepository.findByPlaca(placa);
    }

    //Actualizar un vehiculo
    public VehiculoDTO updateVehiculo(VehiculoDTO vehiculoDTO){
        if(vehiculoRepository.findByPlaca(vehiculoDTO.getPlaca()) != null){
            return vehiculoRepository.update(vehiculoDTO);
        }
        throw new IllegalArgumentException("El vehiculo no existe");
    }

    //Eliminar vehículo
    public boolean deleteVehiculo(String placa){
        Optional<VehiculoDTO> vehiculo = vehiculoRepository.findByPlaca(placa);
        if(!vehiculo.isEmpty()){
            vehiculoRepository.deleteByPlaca(placa);
            return true;
        }
        return false;
    }
    
}
