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
@Table(name = "cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(nullable = false, length = 20, unique = true)
    private String identificacion;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String telefono;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas;
}
