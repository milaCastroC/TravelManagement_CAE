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
            @ApiResponse(responseCode = "400", description = "Datos inválidos o faltantes en la solicitud"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Autenticación requerida"),
            @ApiResponse(responseCode = "403", description = "Prohibido - Permisos insuficientes"),
            @ApiResponse(responseCode = "404", description = "Recursos relacionados no encontrados "),
            @ApiResponse(responseCode = "409", description = "Conflicto - La placa ya existe"),
            @ApiResponse(responseCode = "415", description = "Tipo de contenido no soportado"),
            @ApiResponse(responseCode = "422", description = "Entidad no procesable - Validaciones de negocio fallidas"),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes - Límite de tasa excedido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible")
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
            @ApiResponse(responseCode = "204", description = "No hay vehículos registrados"),
            @ApiResponse(responseCode = "400", description = "Parámetros de consulta inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Autenticación requerida"),
            @ApiResponse(responseCode = "403", description = "Prohibido - Permisos insuficientes"),
            @ApiResponse(responseCode = "406", description = "Formato de respuesta no aceptable"),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes - Límite de tasa excedido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible - En mantenimiento")
    })
    @GetMapping("/all")
    public ResponseEntity<Iterable<VehiculoDTO>> findAll() {
        Iterable<VehiculoDTO> vehiculos = vehiculoService.findAll();
        return new ResponseEntity<>(vehiculos, HttpStatus.OK);
    }

    // Buscar vehículo por placa
    @Operation(summary = "Buscar vehículo por placa", description = "Obtiene un vehículo específico por su número de placa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículo encontrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida - Formato de placa incorrecto"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Se requiere autenticación"),
            @ApiResponse(responseCode = "403", description = "Prohibido - No tiene permisos para esta consulta"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado - No existe registro con la placa proporcionada"),
            @ApiResponse(responseCode = "406", description = "No aceptable - El servidor no puede generar el formato solicitado"),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor "),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible")
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
            @ApiResponse(responseCode = "400", description = "Solicitud inválida - Datos faltantes, formato incorrecto o placa nula"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Autenticación requerida"),
            @ApiResponse(responseCode = "403", description = "Prohibido - Permisos insuficientes para actualización"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado - No existe registro con la placa proporcionada"),
            @ApiResponse(responseCode = "409", description = "Conflicto - Intento de modificar datos únicos (placa) a valores existentes"),
            @ApiResponse(responseCode = "412", description = "Precondición fallida - Versión obsoleta del registro"),
            @ApiResponse(responseCode = "415", description = "Tipo de medio no soportado - Se requiere application/json"),
            @ApiResponse(responseCode = "422", description = "Entidad no procesable - Validaciones de negocio fallidas"),
            @ApiResponse(responseCode = "423", description = "Bloqueado - Vehículo está en estado no editable"),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible")
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
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida - Formato de placa incorrecto"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Autenticación requerida"),
            @ApiResponse(responseCode = "403", description = "Prohibido - Permisos insuficientes para eliminación"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado - Placa no registrada"),
            @ApiResponse(responseCode = "409", description = "Conflicto - Restricciones de integridad "),
            @ApiResponse(responseCode = "423", description = "Bloqueado - Vehículo no puede ser eliminado "),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes "),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible")
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
