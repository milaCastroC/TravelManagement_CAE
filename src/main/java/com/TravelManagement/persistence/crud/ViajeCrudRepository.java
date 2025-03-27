package com.TravelManagement.persistence.crud;

import com.TravelManagement.persistence.entity.Viaje;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ViajeCrudRepository extends CrudRepository<Viaje, Long> {
    //Buscar vehículo por id
    List<Viaje> findByVehiculo_VehiculoId(Long vehiculoId);

    //Verificar que no hayan viajes solapados
    @Query("SELECT v FROM Viaje v WHERE " +
            "v.vehiculo.vehiculoId = :vehiculoId AND " +
            "((v.fechaSalida BETWEEN :fechaInicio AND :fechaFin) OR " +
            "(v.fechaLlegada BETWEEN :fechaInicio AND :fechaFin) OR " +
            "(:fechaInicio BETWEEN v.fechaSalida AND v.fechaLlegada) OR " +
            "(:fechaFin BETWEEN v.fechaSalida AND v.fechaLlegada))")
    List<Viaje> findViajesSolapados(
            @Param("vehiculoId") Long vehiculoId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    //Verificar que exista un viaje y que las reservas no sean vacías
    boolean existsByViajeIdAndReservasIsNotEmpty(Long viajeId);

    //Cantidad de reservas que tiene un viaje
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.viaje.viajeId = :viajeId")
    int countReservasByViajeId(@Param("viajeId") Long viajeId);
}

