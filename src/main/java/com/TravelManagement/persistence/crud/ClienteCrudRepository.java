package com.TravelManagement.persistence.crud;

import com.TravelManagement.persistence.entity.Cliente;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClienteCrudRepository extends CrudRepository<Cliente, Long> {
    //Buscar un cliente por identificacion
    Optional<Cliente> findByIdentificacion(String identificacion);
}
