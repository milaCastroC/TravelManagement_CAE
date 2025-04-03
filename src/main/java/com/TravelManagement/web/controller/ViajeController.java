package com.TravelManagement.web.controller;

import com.TravelManagement.domain.dto.ViajeDTO;
import com.TravelManagement.domain.service.ViajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/viajes")
public class ViajeController {

    @Autowired
    private ViajeService viajeService;

    @Operation(summary = "Registrar nuevo viaje", description = "Crea un nuevo viaje en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Viaje creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos - Faltan campos requeridos o formatos incorrectos"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Autenticación requerida"),
            @ApiResponse(responseCode = "403", description = "Prohibido - No tiene permisos para crear viajes"),
            @ApiResponse(responseCode = "404", description = "Recurso no encontrado - Cliente, vehículo o conductor no existe"),
            @ApiResponse(responseCode = "409", description = "Conflicto - Vehículo/conductor no disponible en fecha/hora solicitada"),
            @ApiResponse(responseCode = "422", description = "Entidad no procesable - Validaciones de negocio fallidas"),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible ")
    })
    @PostMapping("/save")
    public ResponseEntity<?> registrarViaje(@RequestBody ViajeDTO viajeDTO) {
        try {
            ViajeDTO nuevoViaje = viajeService.registrarViaje(viajeDTO);
            return new ResponseEntity<>(nuevoViaje, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Buscar viajes disponibles", description = "Obtiene viajes disponibles filtrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de viajes obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "No hay viajes disponibles con los criterios especificados"),
            @ApiResponse(responseCode = "400", description = "Parámetros de filtrado inválidos o mal formados"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Autenticación requerida"),
            @ApiResponse(responseCode = "403", description = "Prohibido - Permisos insuficientes"),
            @ApiResponse(responseCode = "404", description = "Recursos relacionados no encontrados"),
            @ApiResponse(responseCode = "406", description = "Formato de respuesta no aceptable"),
            @ApiResponse(responseCode = "416", description = "Rango no satisfacible"),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes "),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible")
    })
    @GetMapping("/all")
    public ResponseEntity<Iterable<ViajeDTO>> findAll(){
            Iterable<ViajeDTO> viajes = viajeService.findAll();
            return new ResponseEntity<>(viajes, HttpStatus.OK);
    }

    @Operation(summary = "Actualizar viaje", description = "Actualiza la información de un viaje existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Viaje actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida - Datos faltantes, formato incorrecto o ID nulo"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Autenticación requerida"),
            @ApiResponse(responseCode = "403", description = "Prohibido - Permisos insuficientes para actualización"),
            @ApiResponse(responseCode = "404", description = "Viaje no encontrado - ID no existe"),
            @ApiResponse(responseCode = "409", description = "Conflicto - Intento de modificar datos inmutables o estado inválido"),
            @ApiResponse(responseCode = "412", description = "Precondición fallida - Versión obsoleta del viaje"),
            @ApiResponse(responseCode = "422", description = "Entidad no procesable - Validaciones de negocio fallidas"),
            @ApiResponse(responseCode = "423", description = "Bloqueado - Viaje no editable"),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible ")
    })
    @PutMapping("/update")
    public ResponseEntity<?> actualizarViaje(@RequestBody ViajeDTO viajeDTO) {
        try {
            if (viajeDTO == null || viajeDTO.getViajeId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Datos del viaje incompletos");
            }

            ViajeDTO viajeActualizado = viajeService.actualizarViaje(viajeDTO);
            return ResponseEntity.ok(viajeActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar viaje", description = "Elimina un viaje del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Viaje eliminado exitosamente "),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida - ID mal formado o estado no permitido"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Autenticación requerida"),
            @ApiResponse(responseCode = "403", description = "Prohibido - Rol sin permisos para eliminar viajes"),
            @ApiResponse(responseCode = "404", description = "Viaje no encontrado - ID no existe"),
            @ApiResponse(responseCode = "409", description = "Conflicto - Viaje ya completado o en progreso"),
            @ApiResponse(responseCode = "423", description = "Bloqueado - Viaje asociado a facturación"),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarViaje(@PathVariable Long id) {
        try {
            viajeService.eliminarViaje(id);
            return ResponseEntity.ok("Vehículo eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
