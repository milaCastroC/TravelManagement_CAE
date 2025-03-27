package com.TravelManagement.persistence.crud;

import com.TravelManagement.domain.dto.VehiculoDTO;
import com.TravelManagement.persistence.entity.Vehiculo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VehiculoCrudRepository extends CrudRepository<Vehiculo, Long> {
    //Buscar un vehiculo por placa
    Optional<Vehiculo> findByPlaca(String placa);

    //Validar si un vehículo tiene viajes en el futuro
    @Query("SELECT COUNT(v) > 0 FROM Viaje v WHERE v.vehiculo.placa = :placa AND v.fechaSalida > CURRENT_TIMESTAMP")
    boolean existeViajeFuturoParaVehiculo(String placa);
}
