package com.TravelManagement.domain.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ReservaDTO {

    private Long reservaId;
    private Long viajeId;
    private Long clienteId;
    private String estado;
    private LocalDateTime fechaReserva;

    /*public ReservaDTO(Long reservaId, Long viajeId, Long clienteId, String estado, LocalDateTime fechaReserva) {
        this.reservaId = reservaId;
        this.viajeId = viajeId;
        this.clienteId = clienteId;
        this.estado = estado;
        this.fechaReserva = fechaReserva;
    }*/
}
