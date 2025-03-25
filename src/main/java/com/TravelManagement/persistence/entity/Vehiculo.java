package com.TravelManagement.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@Entity
@Table(name = "vehiculo")
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehiculo_id")
    private Long vehiculoId;

    @Column(nullable = false, length = 10, unique = true)
    private String placa;

    @Column(nullable = false)
    private int capacidad;

    @Column(nullable = false, length = 50)
    private String tipo;

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Viaje> viajes;
}
