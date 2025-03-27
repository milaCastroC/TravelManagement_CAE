package com.TravelManagement.persistence.repositoryImpl;

import com.TravelManagement.domain.dto.VehiculoDTO;
import com.TravelManagement.domain.repository.VehiculoRepository;
import com.TravelManagement.persistence.crud.VehiculoCrudRepository;
import com.TravelManagement.persistence.entity.Vehiculo;
import com.TravelManagement.persistence.mapper.VehiculoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class VehiculoRepositoryImpl implements VehiculoRepository {

    @Autowired
    private VehiculoCrudRepository vehiculoCrudRepository;

    @Autowired
    private VehiculoMapper vehiculoMapper;

    //Guardar un nuevo vehiculo
    @Override
    public VehiculoDTO save(VehiculoDTO vehiculoDTO) {
        Vehiculo vehiculo = vehiculoMapper.toVehiculo(vehiculoDTO);
        if(findByPlaca(vehiculo.getPlaca()).isEmpty()){
            Vehiculo vehiculoSaved = vehiculoCrudRepository.save(vehiculo);
            return vehiculoMapper.toVehiculoDTO(vehiculoSaved);
        }
        throw new IllegalArgumentException(vehiculoDTO.getPlaca() + " ya existe");
    }

    //Buscar vehiculo por placa
    @Override
    public Optional<VehiculoDTO> findByPlaca(String placa) {
        Optional<Vehiculo> vehiculo = vehiculoCrudRepository.findByPlaca(placa);
        return vehiculo.map(vehiculoMapper::toVehiculoDTO);
    }

    @Override
    public Optional<VehiculoDTO> findById(Long vehiculoId) {
        return vehiculoCrudRepository.findById(vehiculoId)
                .map(vehiculoMapper::toVehiculoDTO);
    }

    //Consultar todos los clientes
    @Override
    public Iterable<VehiculoDTO> findAll() {
        Iterable<Vehiculo> vehiculos = vehiculoCrudRepository.findAll();
        return ((List<Vehiculo>) vehiculos).stream()
                .map(vehiculoMapper::toVehiculoDTO)
                .collect(Collectors.toList());
    }

    // Validar si un vehículo tiene viajes futuros
    public boolean tieneViajesFuturos(String placa) {
        return vehiculoCrudRepository.existeViajeFuturoParaVehiculo(placa);
    }

    //Editar un vehículo
    @Override
    public VehiculoDTO update(VehiculoDTO vehiculoDTO) {
        if(vehiculoDTO.getPlaca() == null || vehiculoDTO.getPlaca().isEmpty()){
            throw new IllegalArgumentException(vehiculoDTO.getPlaca() + " ya existe");
        }
        //Validar que no tiene viajes en el futuro
        if (tieneViajesFuturos(vehiculoDTO.getPlaca())) {
            throw new IllegalStateException("No se puede modificar un vehículo con viajes programados.");
        }
        //Buscar vehiculo por identificación
        Vehiculo vehiculoExistente = vehiculoCrudRepository.findByPlaca(vehiculoDTO.getPlaca())
                .orElseThrow(() -> new IllegalArgumentException(vehiculoDTO.getPlaca() + " no existe"));

        //Actualizar solo los campos permitidos
        vehiculoExistente.setCapacidad(vehiculoDTO.getCapacidad());
        vehiculoExistente.setTipo(vehiculoDTO.getTipo());

        //Guardar el vehiculo actualizado
        Vehiculo updatedVehiculo = vehiculoCrudRepository.save(vehiculoExistente);

        //Convertir y retornar el vehiculo actualizado como DTO
        return vehiculoMapper.toVehiculoDTO(updatedVehiculo);
    }

    @Override
    public void deleteByPlaca(String placa) {
        //Verificar que existe
        Vehiculo vehiculo = vehiculoCrudRepository.findByPlaca(placa)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con placa: " + placa));

        //Verificar que no tiene viajes en el futuro
        if (tieneViajesFuturos(placa)) {
            throw new IllegalStateException("No se puede eliminar un vehículo con viajes programados en el futuro.");
        }
        vehiculoCrudRepository.delete(vehiculo);
    }

}
