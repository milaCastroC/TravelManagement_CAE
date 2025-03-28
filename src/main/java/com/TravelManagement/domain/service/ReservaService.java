package com.TravelManagement.domain.service;

import com.TravelManagement.domain.dto.ClienteDTO;
import com.TravelManagement.domain.dto.ReservaDTO;
import com.TravelManagement.domain.dto.ViajeDTO;
import com.TravelManagement.domain.repository.ClienteRepository;
import com.TravelManagement.domain.repository.ReservaRepository;
import com.TravelManagement.domain.repository.ViajeRepository;
import com.TravelManagement.persistence.entity.EstadoReserva;
import com.TravelManagement.persistence.mapper.ReservaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ReservaMapper reservaMapper;

    // Crear una nueva reserva
    public ReservaDTO crearReserva(ReservaDTO reservaDTO) {
        //Validar que el cliente exista
        ClienteDTO cliente = clienteRepository.findById(reservaDTO.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        // Validar existencia del viaje y obtener datos
        ViajeDTO viaje = viajeRepository.findById(reservaDTO.getViajeId())
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

        // Validar que la fecha de salida no haya pasado
        if (viaje.getFechaSalida().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede reservar un viaje cuya hora de salida ya ha pasado");
        }

        // Validar disponibilidad de asientos
        if (viaje.getAsientosDisponibles() <= 0) {
            throw new IllegalArgumentException("No hay asientos disponibles para este viaje");
        }

        // Validar que el cliente no tenga ya una reserva para este viaje
        Optional<ReservaDTO> reservaExistente = reservaRepository.findByViajeIdAndClienteId(reservaDTO.getViajeId(), reservaDTO.getClienteId());
        if (reservaExistente.isPresent()) {
            throw new IllegalArgumentException("El cliente ya tiene una reserva para este viaje");
        }

        // Setear estado inicial de la reserva (por ejemplo, PENDIENTE)
        reservaDTO.setEstado(EstadoReserva.pendiente.name());
        // La fecha de reserva se asigna en la entidad automáticamente, pero se puede asignar aquí si se desea.
        return reservaRepository.save(reservaDTO);
    }

    // Consultar reservas (todas o por cliente)
    public Iterable<ReservaDTO> findAll() {
        return reservaRepository.findAll();
    }

    public List<ReservaDTO> findByClienteId(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId);
    }

    // Actualizar una reserva (solo si el viaje aún no ha iniciado y la reserva no está en curso, finalizada o cancelada)
    public ReservaDTO actualizarReserva(ReservaDTO reservaDTO) {
        // Verificar que la reserva existe
        ReservaDTO reservaExistente = reservaRepository.findById(reservaDTO.getReservaId())
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        // Obtener el viaje para validar que aún no inició
        ViajeDTO viaje = viajeRepository.findById(reservaExistente.getViajeId())
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

        if (viaje.getFechaSalida().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede modificar una reserva de un viaje que ya inició");
        }

        // Se actualizará el estado u otros datos permitidos.
        return reservaRepository.update(reservaDTO);
    }

    // Cancelar una reserva
    public void cancelarReserva(Long reservaId) {
        ReservaDTO reservaExistente = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        // Obtener el viaje para validar que no haya iniciado
        ViajeDTO viaje = viajeRepository.findById(reservaExistente.getViajeId())
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

        if (viaje.getFechaSalida().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede cancelar una reserva de un viaje que ya inició");
        }

        // Solo se pueden cancelar reservas en estado PENDIENTE o CONFIRMADA
        EstadoReserva estadoActual = EstadoReserva.valueOf(reservaExistente.getEstado());
        if (!(estadoActual == EstadoReserva.pendiente || estadoActual == EstadoReserva.confirmada)) {
            throw new IllegalArgumentException("Solo se pueden cancelar reservas en estado Pendiente o Confirmada");
        }

        // Validar que no esté ya cancelada
        if (estadoActual == EstadoReserva.cancelado) {
            throw new IllegalStateException("La reserva ya está cancelada");
        }

        // Actualizamos el estado a CANCELADA
        reservaRepository.cancelar(reservaId);
    }

    // Método para actualizar el estado de una reserva (control de estado)
    public ReservaDTO actualizarEstadoReserva(Long reservaId, String nuevoEstado) {
        ReservaDTO reservaExistente = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        // Se pueden incluir validaciones adicionales según la lógica de negocio.
        reservaExistente.setEstado(nuevoEstado);
        return reservaRepository.update(reservaExistente);
    }

}
