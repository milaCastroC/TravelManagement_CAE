package com.TravelManagement.web.controller;

import com.TravelManagement.domain.dto.ReservaDTO;
import com.TravelManagement.domain.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    // Crear una nueva reserva
    @Operation(summary = "Crear reserva", description = "Realiza una reserva a un cliente para un viaje disponible")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos de reserva")
    })
    @PostMapping("/save")
    public ResponseEntity<?> crearReserva(@RequestBody ReservaDTO reservaDTO) {
        try {
            ReservaDTO nuevaReserva = reservaService.crearReserva(reservaDTO);
            return new ResponseEntity<>(nuevaReserva, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Consultar todas las reservas
    @Operation(summary = "Consultar reservas", description = "Obtiene la lista de todas las reservas (activas y pasadas)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservas obtenidas exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/all")
    public ResponseEntity<Iterable<ReservaDTO>> findAll() {
        Iterable<ReservaDTO> reservas = reservaService.findAll();
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    // Consultar reservas por cliente
    @Operation(summary = "Consultar reservas por cliente", description = "Obtiene la lista de reservas de un cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservas del cliente obtenidas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente o reservas no encontrados")
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> findByCliente(@PathVariable Long clienteId) {
        try {
            List<ReservaDTO> reservas = reservaService.findByClienteId(clienteId);
            return ResponseEntity.ok(reservas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Actualizar reserva (solo si el viaje aún no inició)
    @Operation(summary = "Actualizar reserva", description = "Modifica la información de una reserva si el viaje aún no ha iniciado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos o reserva no modificable"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PutMapping("/update")
    public ResponseEntity<?> actualizarReserva(@RequestBody ReservaDTO reservaDTO) {
        try {
            ReservaDTO reservaActualizada = reservaService.actualizarReserva(reservaDTO);
            return ResponseEntity.ok(reservaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Cancelar reserva
    @Operation(summary = "Cancelar reserva", description = "Cancela una reserva si cumple los criterios (estado Pendiente o Confirmada y el viaje no ha iniciado)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente"),
            @ApiResponse(responseCode = "400", description = "No se puede cancelar la reserva"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PatchMapping("/cancel/{id}")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id) {
        try {
            reservaService.cancelarReserva(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
