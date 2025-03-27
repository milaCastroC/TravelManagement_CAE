package com.TravelManagement.domain.repository;

import com.TravelManagement.domain.dto.ViajeDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ViajeRepository {

    //Guardar un nuevo viaje
    ViajeDTO save(ViajeDTO viajeDTO);

    //Buscar viaje por id
    Optional<ViajeDTO> findById(Long id);

    //Buscar todos los viajes
    Iterable<ViajeDTO> findAll();

    //Actualizar un viaje
    ViajeDTO update(ViajeDTO viajeDTO);

    //Eliminar un viaje
    void delete(Long id);

    //Verificar si hay reservas activas
    boolean tieneReservasActivas(Long viajeId);

    //Verificar que los viajes no se solapen
    boolean existeViajeSolapado(Long vehiculoId, LocalDateTime fechaSalida, LocalDateTime fechaLlegada);

    //Contar las reservas que tiene el viaje
    int countReservasByViajeId(Long viajeId);
}
