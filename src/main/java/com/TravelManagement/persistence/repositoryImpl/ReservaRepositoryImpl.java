package com.TravelManagement.persistence.repositoryImpl;

import com.TravelManagement.domain.dto.ReservaDTO;
import com.TravelManagement.domain.repository.ReservaRepository;
import com.TravelManagement.persistence.crud.ReservaCrudRepository;
import com.TravelManagement.persistence.entity.EstadoReserva;
import com.TravelManagement.persistence.entity.Reserva;
import com.TravelManagement.persistence.mapper.ReservaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ReservaRepositoryImpl implements ReservaRepository {

    @Autowired
    private ReservaCrudRepository reservaCrudRepository;

    @Autowired
    private ReservaMapper reservaMapper;

    @Override
    public ReservaDTO save(ReservaDTO reservaDTO) {
        Reserva reserva = reservaMapper.toReserva(reservaDTO);
        Reserva savedReserva = reservaCrudRepository.save(reserva);
        return reservaMapper.toReservaDto(savedReserva);
    }

    @Override
    public Optional<ReservaDTO> findById(Long reservaId) {
        return reservaCrudRepository.findById(reservaId)
                .map(reservaMapper::toReservaDto);
    }

    @Override
    public Iterable<ReservaDTO> findAll() {
        Iterable<Reserva> reservas = reservaCrudRepository.findAll();
        return ((List<Reserva>) reservas).stream()
                .map(reservaMapper::toReservaDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservaDTO> findByClienteId(Long clienteId) {
        List<Reserva> reservas = reservaCrudRepository.findByClienteId(clienteId);
        return reservas.stream()
                .map(reservaMapper::toReservaDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReservaDTO update(ReservaDTO reservaDTO) {
        // Se asume que la validación se realiza en el servicio
        Reserva reservaExistente = reservaCrudRepository.findById(reservaDTO.getReservaId())
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        // Actualizar solo campos permitidos (por ejemplo, estado)
        reservaExistente.setEstado(
                reservaDTO.getEstado() != null ?
                        Enum.valueOf(com.TravelManagement.persistence.entity.EstadoReserva.class, reservaDTO.getEstado())
                        : reservaExistente.getEstado());
        Reserva updatedReserva = reservaCrudRepository.save(reservaExistente);
        return reservaMapper.toReservaDto(updatedReserva);
    }

    @Override
    public void cancelar(Long reservaId) {
        Reserva reserva = reservaCrudRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Validar que no esté ya cancelada
        if (reserva.getEstado() == EstadoReserva.cancelado) {
            throw new IllegalStateException("La reserva ya está cancelada");
        }

        reserva.setEstado(EstadoReserva.cancelado);
        reservaCrudRepository.save(reserva);
    }

    @Override
    public void eliminar(Long reservaId) {
        reservaCrudRepository.deleteById(reservaId);
    }

    @Override
    public Optional<ReservaDTO> findByViajeIdAndClienteId(Long viajeId, Long clienteId) {
        return reservaCrudRepository.findByViajeIdAndClienteId(viajeId, clienteId)
                .map(reservaMapper::toReservaDto);
    }
}
