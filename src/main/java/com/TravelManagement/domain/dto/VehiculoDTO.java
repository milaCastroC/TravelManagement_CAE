package com.TravelManagement.domain.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class VehiculoDTO {

    private Long vehiculoId;
    private String placa;
    private int capacidad;
    private String tipo;

    public VehiculoDTO(Long vehiculoId, String placa, int capacidad, String tipo) {
        this.vehiculoId = vehiculoId;
        this.placa = placa;
        this.capacidad = capacidad;
        this.tipo = tipo;
    }
}
