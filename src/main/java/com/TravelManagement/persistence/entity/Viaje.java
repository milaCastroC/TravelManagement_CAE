package com.TravelManagement.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@Entity
@Table(name = "viaje")
public class Viaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "viaje_id")
    private Long viajeId;

    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @Column(nullable = false, length = 50)
    private String origen;

    @Column(nullable = false, length = 50)
    private String destino;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDateTime fechaSalida;

    @Column(name = "fecha_llegada", nullable = false)
    private LocalDateTime fechaLlegada;

    @Column(nullable = false)
    private Double precio;

    @OneToMany(mappedBy = "viaje", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas;
}
