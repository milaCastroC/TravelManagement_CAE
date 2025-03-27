package com.TravelManagement.persistence.repositoryImpl;

import com.TravelManagement.domain.dto.ViajeDTO;
import com.TravelManagement.domain.repository.ViajeRepository;
import com.TravelManagement.persistence.crud.VehiculoCrudRepository;
import com.TravelManagement.persistence.crud.ViajeCrudRepository;
import com.TravelManagement.persistence.entity.Vehiculo;
import com.TravelManagement.persistence.entity.Viaje;
import com.TravelManagement.persistence.mapper.ViajeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ViajeRepositoryImpl implements ViajeRepository {

    @Autowired
    private ViajeCrudRepository viajeCrudRepository;

    @Autowired
    private ViajeMapper viajeMapper;
    @Autowired
    private VehiculoCrudRepository vehiculoCrudRepository;

    //Guardar un viaje
    @Override
    public ViajeDTO save(ViajeDTO viajeDTO) {
        // Cargar el vehículo completo
        Vehiculo vehiculo = vehiculoCrudRepository.findById(viajeDTO.getVehiculoId())
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado"));

        Viaje viaje = viajeMapper.toViaje(viajeDTO);
        viaje.setVehiculo(vehiculo); // Asegurar que el vehículo está establecido

        Viaje savedViaje = viajeCrudRepository.save(viaje);
        return viajeMapper.toViajeDto(savedViaje);
    }

    //Buscar viaje por id
    @Override
    public Optional<ViajeDTO> findById(Long id) {
        return viajeCrudRepository.findById(id)
                .map(viajeMapper::toViajeDto);
    }

    //Buscar todos los viajes
    @Override
    public Iterable<ViajeDTO> findAll() {
        Iterable<Viaje> viajes = viajeCrudRepository.findAll();
        return((List<Viaje>) viajes).stream()
                .map(viajeMapper::toViajeDto)
                .collect(Collectors.toList());
    }

    //Actualizar un viaje
    @Override
    public ViajeDTO update(ViajeDTO viajeDTO) {
        Viaje viajeExistente = viajeCrudRepository.findById(viajeDTO.getViajeId())
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado"));

        // Actualizar campos permitidos
        viajeExistente.setFechaSalida(viajeDTO.getFechaSalida());
        viajeExistente.setFechaLlegada(viajeDTO.getFechaLlegada());
        viajeExistente.setPrecio(viajeDTO.getPrecio());

        Viaje updatedViaje = viajeCrudRepository.save(viajeExistente);
        return viajeMapper.toViajeDto(updatedViaje);
    }

    //Eliminar un viaje
    @Override
    public void delete(Long id) {
        viajeCrudRepository.deleteById(id);
    }

    //Verificar si hay reservas activas
    @Override
    public boolean tieneReservasActivas(Long viajeId) {
        return viajeCrudRepository.existsByViajeIdAndReservasIsNotEmpty(viajeId);
    }

    @Override
    //Verificar que los viajes no se solapen
    public boolean existeViajeSolapado(Long vehiculoId, LocalDateTime fechaSalida, LocalDateTime fechaLlegada) {
        // Asegúrate de que esta consulta existe en ViajeCrudRepository
        List<Viaje> viajesSolapados = viajeCrudRepository.findViajesSolapados(
                vehiculoId,
                fechaSalida,
                fechaLlegada
        );
        return !viajesSolapados.isEmpty();
    }

    @Override
    public int countReservasByViajeId(Long viajeId) {
        return viajeCrudRepository.countReservasByViajeId(viajeId);
    }


}
