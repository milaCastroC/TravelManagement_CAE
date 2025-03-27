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
public class ViajeDTO {
    private Long viajeId;
    private Long vehiculoId;
    private String origen;
    private String destino;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegada;
    private Double precio;

    // Campos calculados o de visualización
    private String placaVehiculo;
    private Integer capacidadVehiculo;
    private Integer asientosDisponibles;

    public ViajeDTO(Long viajeId, Long vehiculoId, String origen, String destino, LocalDateTime fechaSalida, LocalDateTime fechaLlegada, Double precio) {
        this.viajeId = viajeId;
        this.vehiculoId = vehiculoId;
        this.origen = origen;
        this.destino = destino;
        this.fechaSalida = fechaSalida;
        this.fechaLlegada = fechaLlegada;
        this.precio = precio;
    }
}
