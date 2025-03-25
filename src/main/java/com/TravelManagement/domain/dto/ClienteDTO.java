package com.TravelManagement.domain.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ClienteDTO {

    private Long clienteId;
    private String identificacion;
    private String nombre;
    private String email;
    private String telefono;

    public ClienteDTO(Long clienteId, String identificacion, String nombre, String email, String telefono){
        this.clienteId = clienteId;
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }
}
