package com.TravelManagement.domain.service;

import com.TravelManagement.domain.dto.VehiculoDTO;
import com.TravelManagement.domain.dto.ViajeDTO;
import com.TravelManagement.domain.repository.VehiculoRepository;
import com.TravelManagement.domain.repository.ViajeRepository;
import com.TravelManagement.persistence.mapper.ViajeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ViajeService {

    private static final Logger log = LoggerFactory.getLogger(ViajeService.class);

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private ViajeMapper viajeMapper;

    private void validarViaje(ViajeDTO viajeDTO) {
        log.info("Buscando vehículo con ID: {}", viajeDTO.getVehiculoId());
        // Validar fechas
        if (viajeDTO.getFechaSalida().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de salida no puede ser en el pasado");
        }

        if (viajeDTO.getFechaLlegada().isBefore(viajeDTO.getFechaSalida())) {
            throw new IllegalArgumentException("La fecha de llegada debe ser posterior a la fecha de salida");
        }

        // Validar origen y destino
        if (viajeDTO.getOrigen().equalsIgnoreCase(viajeDTO.getDestino())) {
            throw new IllegalArgumentException("El origen y destino no pueden ser iguales");
        }

        // Validar vehículo
        VehiculoDTO vehiculo = vehiculoRepository.findById(viajeDTO.getVehiculoId())
                .orElseThrow(() -> {
                    log.error("Vehículo con ID {} no encontrado", viajeDTO.getVehiculoId());
                    return new IllegalArgumentException("Vehículo no encontrado");
                });

        // Validar solapamiento de horarios
        if (viajeRepository.existeViajeSolapado(
                vehiculo.getVehiculoId(),
                viajeDTO.getFechaSalida(),
                viajeDTO.getFechaLlegada()
        )) {
            throw new IllegalArgumentException("El vehículo ya tiene un viaje programado en ese horario");
        }
    }

    private void validarNuevoVehiculo(ViajeDTO viajeDTO) {
        //Validar que el vehiculo no sea nulo
        if (viajeDTO.getVehiculoId() == null) {
            throw new IllegalArgumentException("El ID del vehículo no puede ser nulo");
        }

        // Verificar que el nuevo vehículo existe
        VehiculoDTO vehiculo = vehiculoRepository.findById(viajeDTO.getVehiculoId())
                .orElseThrow(() -> new IllegalArgumentException("Nuevo vehículo no encontrado"));

        // Verificar que el vehículo tiene capacidad suficiente
        int reservasActuales = viajeRepository.countReservasByViajeId(viajeDTO.getViajeId());
        if (vehiculo.getCapacidad() < reservasActuales) {
            throw new IllegalArgumentException("El nuevo vehículo no tiene capacidad suficiente");
        }

        // Verificar disponibilidad del nuevo vehículo en las nuevas fechas
        if (viajeRepository.existeViajeSolapado(
                vehiculo.getVehiculoId(),
                viajeDTO.getFechaSalida(),
                viajeDTO.getFechaLlegada()
        )) {
            throw new IllegalArgumentException("El nuevo vehículo no está disponible en ese horario");
        }
    }

    public ViajeDTO registrarViaje(ViajeDTO viajeDTO) {
        log.info("Intentando registrar viaje con vehículoId: {}", viajeDTO.getVehiculoId());
        Optional<VehiculoDTO> vehiculo = vehiculoRepository.findById(viajeDTO.getVehiculoId());
        log.info("Vehículo encontrado en BD: {}", vehiculo.isPresent());

        validarViaje(viajeDTO);
        return viajeRepository.save(viajeDTO);
    }

    public Iterable<ViajeDTO> findAll(){
        return viajeRepository.findAll();
    }

    public ViajeDTO actualizarViaje(ViajeDTO viajeDTO) {
        // Validar que el viaje existe
        ViajeDTO viajeExistente = viajeRepository.findById(viajeDTO.getViajeId())
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

        // Agregar validación de fechas similar a registrarViaje
        if (viajeDTO.getFechaSalida().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de salida no puede ser en el pasado");
        }

        if (viajeDTO.getFechaLlegada().isBefore(viajeDTO.getFechaSalida())) {
            throw new IllegalArgumentException("La fecha de llegada debe ser posterior a la fecha de salida");
        }

        // Validar que no está en curso o finalizado
        if (viajeExistente.getFechaSalida().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede modificar un viaje en curso o finalizado");
        }

        // Validar que no hay reservas si se cambia origen/destino
        if ((!viajeExistente.getOrigen().equals(viajeDTO.getOrigen()) ||
                !viajeExistente.getDestino().equals(viajeDTO.getDestino())) &&
                viajeRepository.tieneReservasActivas(viajeDTO.getViajeId())) {
            throw new IllegalArgumentException("No se puede cambiar origen/destino con reservas activas");
        }

        // Validar nuevo vehículo si cambió
        if (!viajeExistente.getVehiculoId().equals(viajeDTO.getVehiculoId())) {
            validarNuevoVehiculo(viajeDTO);
        }

        return viajeRepository.update(viajeDTO);
    }

    public void eliminarViaje(Long id) {
        ViajeDTO viaje = viajeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

        if (viaje.getFechaSalida().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede eliminar un viaje en curso o finalizado");
        }

        if (viajeRepository.tieneReservasActivas(id)) {
            throw new IllegalArgumentException("No se puede eliminar un viaje con reservas activas");
        }

        viajeRepository.delete(id);
    }
}
