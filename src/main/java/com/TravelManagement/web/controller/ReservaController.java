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
            @ApiResponse(responseCode = "400", description = "Datos inválidos - Campos faltantes o formato incorrecto"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Autenticación requerida"),
            @ApiResponse(responseCode = "403", description = "Prohibido - No tiene permisos para reservar"),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado - Cliente, viaje o vehículo no existe"),
            @ApiResponse(responseCode = "409", description = "Conflicto - Viaje no disponible o fechas ocupadas"),
            @ApiResponse(responseCode = "422", description = "Entidad no procesable - Validaciones de negocio fallidas"),
            @ApiResponse(responseCode = "423", description = "Bloqueado - Cliente con reservas pendientes "),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible ")
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
            @ApiResponse(responseCode = "200", description = "Listado de reservas obtenido exitosamente"),
            @ApiResponse(responseCode = "204", description = "No hay reservas registradas en el sistema"),
            @ApiResponse(responseCode = "400", description = "Parámetros de consulta inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Autenticación requerida"),
            @ApiResponse(responseCode = "403", description = "Prohibido - Permisos insuficientes para consultar reservas"),
            @ApiResponse(responseCode = "406", description = "Formato de respuesta no aceptable"),
            @ApiResponse(responseCode = "416", description = "Rango no válido"),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible")
    })
    @GetMapping("/all")
    public ResponseEntity<Iterable<ReservaDTO>> findAll() {
        Iterable<ReservaDTO> reservas = reservaService.findAll();
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    // Consultar reservas por cliente
    @Operation(summary = "Consultar reservas por cliente", description = "Obtiene la lista de reservas de un cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas del cliente obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "El cliente no tiene reservas registradas"),
            @ApiResponse(responseCode = "400", description = "ID de cliente inválido o mal formado"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Autenticación requerida"),
            @ApiResponse(responseCode = "403", description = "Prohibido - No tiene permisos para ver estas reservas"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "406", description = "Formato de respuesta no aceptable"),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible")
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
                    @ApiResponse(responseCode = "400", description = "Datos inválidos - Campos faltantes, formato incorrecto o ID nulo"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Autenticación requerida"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Permisos insuficientes para modificar reservas"),
                    @ApiResponse(responseCode = "404", description = "Reserva no encontrada - ID no existe"),
                    @ApiResponse(responseCode = "409", description = "Conflicto - Viaje ya iniciado o estado no modificable"),
                    @ApiResponse(responseCode = "412", description = "Precondición fallida - Versión obsoleta de la reserva"),
                    @ApiResponse(responseCode = "422", description = "Entidad no procesable - Validaciones de negocio fallidas"),
                    @ApiResponse(responseCode = "423", description = "Bloqueado - transacción en proceso"),
                    @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes "),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
                    @ApiResponse(responseCode = "503", description = "Servicio no disponible")
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
            @ApiResponse(responseCode = "400", description = "Solicitud inválida - ID mal formado o estado no cancelable"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Autenticación requerida"),
            @ApiResponse(responseCode = "403", description = "Prohibido - Permisos insuficientes para cancelar"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada - ID no existe"),
            @ApiResponse(responseCode = "409", description = "Conflicto - Viaje ya iniciado o reserva no cancelable"),
            @ApiResponse(responseCode = "423", description = "Bloqueado - Proceso en curso"),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes "),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible")
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

    //Eliminar reserva
    @Operation(summary = "Eliminar reserva", description = "Elimina un reserva del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reserva eliminada exitosamente - No retorna contenido"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida - ID mal formado o reserva no eliminable"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Autenticación requerida"),
            @ApiResponse(responseCode = "403", description = "Prohibido - Permisos insuficientes para eliminar"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada - ID no existe"),
            @ApiResponse(responseCode = "409", description = "Conflicto - Reserva asociada a un viaje en progreso o completado"),
            @ApiResponse(responseCode = "423", description = "Bloqueado - Reserva vinculada a procesos pendientes"),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes "),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible")
    })
    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> eliminarReserva(@PathVariable Long id) {
        try {
            reservaService.eliminarReserva(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
