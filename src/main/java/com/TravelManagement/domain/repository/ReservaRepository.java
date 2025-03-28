package com.TravelManagement.domain.repository;

import com.TravelManagement.domain.dto.ReservaDTO;

import java.util.List;
import java.util.Optional;

public interface ReservaRepository {

    // Guardar una nueva reserva
    ReservaDTO save(ReservaDTO reservaDTO);

    // Buscar reserva por id
    Optional<ReservaDTO> findById(Long reservaId);

    // Listar todas las reservas (activas y pasadas)
    Iterable<ReservaDTO> findAll();

    // Listar reservas de un cliente
    List<ReservaDTO> findByClienteId(Long clienteId);

    // Actualizar una reserva
    ReservaDTO update(ReservaDTO reservaDTO);

    // Eliminar una reserva (o cancelar)
    void cancelar(Long reservaId);

    // Verificar si ya existe una reserva para el mismo viaje y cliente
    Optional<ReservaDTO> findByViajeIdAndClienteId(Long viajeId, Long clienteId);
}
