package com.TravelManagement.persistence.crud;

import com.TravelManagement.persistence.entity.Cliente;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClienteCrudRepository extends CrudRepository<Cliente, Long> {

    Optional<Cliente> findByIdentificacion(String identificacion);

    boolean existsClienteByIdentificacionAndReservasEstado(String identificacion, String estado);
}
