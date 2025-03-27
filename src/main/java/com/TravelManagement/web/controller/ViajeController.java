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
            @ApiResponse(responseCode = "400", description = "Datos del viaje inválidos")
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
            @ApiResponse(responseCode = "200", description = "Lista de viajes obtenida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/all")
    public ResponseEntity<Iterable<ViajeDTO>> findAll(){
            Iterable<ViajeDTO> viajes = viajeService.findAll();
            return new ResponseEntity<>(viajes, HttpStatus.OK);
    }

    @Operation(summary = "Actualizar viaje", description = "Actualiza la información de un viaje existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Viaje actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Viaje no encontrado")
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
            @ApiResponse(responseCode = "204", description = "Viaje eliminado"),
            @ApiResponse(responseCode = "400", description = "No se puede eliminar el viaje"),
            @ApiResponse(responseCode = "404", description = "Viaje no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarViaje(@PathVariable Long id) {
        try {
            viajeService.eliminarViaje(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
