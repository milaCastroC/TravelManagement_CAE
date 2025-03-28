package com.TravelManagement.persistence.crud;

import com.TravelManagement.persistence.entity.Reserva;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservaCrudRepository extends CrudRepository<Reserva, Long> {
    // Buscar reservas de un cliente
    @Query("SELECT r FROM Reserva r WHERE r.cliente.clienteId = :clienteId")
    List<Reserva> findByClienteId(@Param("clienteId") Long clienteId);

    // Verificar si ya existe una reserva para un viaje y cliente
    @Query("SELECT r FROM Reserva r WHERE r.viaje.viajeId = :viajeId AND r.cliente.clienteId = :clienteId")
    Optional<Reserva> findByViajeIdAndClienteId(@Param("viajeId") Long viajeId, @Param("clienteId") Long clienteId);
}
