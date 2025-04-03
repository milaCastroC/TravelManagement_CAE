package com.TravelManagement.web.controller;

import com.TravelManagement.domain.dto.ClienteDTO;
import com.TravelManagement.domain.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    //Guardar un cliente
    @Operation(summary = "Guardar un nuevo cliente", description = "Guardar un nuevo cliente en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Se requiere autenticación para esta operación"),
            @ApiResponse(responseCode = "403", description = "Prohibido - No tiene permisos para crear clientes"),
            @ApiResponse(responseCode = "404", description = "No encontrado - Recurso relacionado no existe "),
            @ApiResponse(responseCode = "406", description = "No aceptable - El servidor no puede producir la respuesta en el formato solicitado"),
            @ApiResponse(responseCode = "409", description = "Conflicto - La identificación, email o datos únicos ya están registrados"),
            @ApiResponse(responseCode = "415", description = "Tipo de medio no soportado - Content-Type diferente a application/json"),
            @ApiResponse(responseCode = "422", description = "Entidad no procesable - Validación de negocio fallida"),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible ")
    })
    @PostMapping("/save")
    public ResponseEntity<ClienteDTO> saveCliente(@RequestBody ClienteDTO clienteDTO){
        ClienteDTO savedCliente = clienteService.save(clienteDTO);
        return new ResponseEntity<>(savedCliente, HttpStatus.CREATED);
    }

    //Consultar todos los clientes
    @Operation(summary = "Consultar todos los clientes", description = "Consultar todos los clientes en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "No hay contenido - No existen clientes registrados en el sistema"),
            @ApiResponse(responseCode = "206", description = "Contenido parcial"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida "),
            @ApiResponse(responseCode = "401", description = "No autorizado - Se requiere autenticación para acceder al recurso"),
            @ApiResponse(responseCode = "403", description = "Prohibido - No tiene permisos para ver clientes"),
            @ApiResponse(responseCode = "406", description = "No aceptable - El servidor no puede generar el formato solicitado "),
            @ApiResponse(responseCode = "410", description = "Este endpoint ha sido movido permanentemente a otra URI"),
            @ApiResponse(responseCode = "416", description = "Rango no satisfacible - fuera de rango"),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes "),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor "),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible ")
    })
    @GetMapping("/all")
    public ResponseEntity<Iterable<ClienteDTO>> findAll(){
        Iterable<ClienteDTO> clientes = clienteService.findAll();
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    //Consultar cliente por identificación
    @Operation(summary = "Buscar cliente por identificación", description = "Busca un cliente usando su número de identificación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Identificación inválida - Formato incorrecto o parámetro vacío"),
            @ApiResponse(responseCode = "401", description = "No autorizado - Se requiere autenticación"),
            @ApiResponse(responseCode = "403", description = "Prohibido - El usuario no tiene permisos para acceder a este recurso"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado - No existe registro con esa identificación"),
            @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes "),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor - Problema al procesar la solicitud"),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible")
    })
    @GetMapping("/find/{identificacion}")
    public ResponseEntity<ClienteDTO> findByIdentificacion(@PathVariable String identificacion){
        Optional<ClienteDTO> cliente = clienteService.findByIdentificacion(identificacion);

        return cliente.map(clienteDTO -> new ResponseEntity<>(clienteDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    //Actualizar un cliente
    @Operation(summary = "Actualizar cliente", description = "Actualiza la información de un cliente existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Prohibido - No tiene los permisos necesarios"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto - Intento de modificar datos únicos"),
            @ApiResponse(responseCode = "415", description = "Tipo de medio no soportado - El content-type no es application/json"),
            @ApiResponse(responseCode = "422", description = "Entidad no procesable - Los datos son válidos pero no se pueden procesar"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/update")
    public ResponseEntity<ClienteDTO> updateCliente(@RequestBody ClienteDTO clienteDTO) {
        try {
            // Validar que el DTO no sea nulo y tenga identificación
            if (clienteDTO == null || clienteDTO.getIdentificacion() == null) {
                return ResponseEntity.badRequest().build();
            }

            // Realizar actualización
            ClienteDTO updatedCliente = clienteService.updateCliente(clienteDTO);
            return ResponseEntity.ok(updatedCliente);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Eliminar cliente
    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente eliminado exitosamente"),
            @ApiResponse(responseCode = "204", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "No autorizado "),
            @ApiResponse(responseCode = "403", description = "Prohibido - No tiene permisos para esta operación"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "409", description = "El cliente no puede ser eliminado por restricciones"),
            @ApiResponse(responseCode = "423", description = "El cliente está bloqueado y no puede ser eliminado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor "),
            @ApiResponse(responseCode = "503", description = "Servicio no disponible")
    })
    @DeleteMapping("/delete/{identificacion}")
    public ResponseEntity<String> deleteCliente(@PathVariable String identificacion) {
        try {
            boolean eliminado = clienteService.deleteCliente(identificacion);
            return ResponseEntity.ok("Cliente eliminado exitosamente");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Cliente no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}