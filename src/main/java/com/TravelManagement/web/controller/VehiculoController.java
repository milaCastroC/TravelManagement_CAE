package com.TravelManagement.web.controller;

import com.TravelManagement.domain.dto.VehiculoDTO;
import com.TravelManagement.domain.service.VehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoService vehiculoService;

    // Guardar un vehículo
    @Operation(summary = "Guardar un nuevo vehículo", description = "Registra un nuevo vehículo en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehículo creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El vehículo ya existe o datos inválidos")
    })
    @PostMapping("/save")
    public ResponseEntity<?> saveVehiculo(@RequestBody VehiculoDTO vehiculoDTO) {
        try {
            VehiculoDTO savedVehiculo = vehiculoService.save(vehiculoDTO);
            return new ResponseEntity<>(savedVehiculo, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener todos los vehículos
    @Operation(summary = "Consultar todos los vehículos", description = "Obtiene la lista de todos los vehículos registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de vehículos obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/all")
    public ResponseEntity<Iterable<VehiculoDTO>> findAll() {
        Iterable<VehiculoDTO> vehiculos = vehiculoService.findAll();
        return new ResponseEntity<>(vehiculos, HttpStatus.OK);
    }

    // Buscar vehículo por placa
    @Operation(summary = "Buscar vehículo por placa", description = "Obtiene un vehículo específico por su número de placa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículo encontrado"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    @GetMapping("/find/{placa}")
    public ResponseEntity<VehiculoDTO> findByPlaca(@PathVariable String placa) {
        Optional<VehiculoDTO> vehiculo = vehiculoService.findByPlaca(placa);
        return vehiculo.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Actualizar un vehículo
    @Operation(summary = "Actualizar un vehículo", description = "Actualiza la información de un vehículo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículo actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para actualización")
    })
    @PutMapping("/update")
    public ResponseEntity<VehiculoDTO> updateVehiculo(@RequestBody VehiculoDTO vehiculoDTO) {
        try {
            if (vehiculoDTO == null || vehiculoDTO.getPlaca() == null) {
                return ResponseEntity.badRequest().build();
            }
            VehiculoDTO updatedVehiculo = vehiculoService.updateVehiculo(vehiculoDTO);
            return ResponseEntity.ok(updatedVehiculo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar un vehículo
    @Operation(summary = "Eliminar un vehículo", description = "Elimina un vehículo del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículo eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    @DeleteMapping("/delete/{placa}")
    public ResponseEntity<String> deleteVehiculo(@PathVariable String placa) {
        boolean eliminado = vehiculoService.deleteVehiculo(placa);
        if (eliminado) {
            return ResponseEntity.ok("Vehículo eliminado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehículo no encontrado");
        }
    }


}
