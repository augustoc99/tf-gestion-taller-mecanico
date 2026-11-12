package com.taller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String nombre,

        @Size(max = 20, message = "El DNI/CUIT no puede superar 20 caracteres")
        String dniCuit,

        @Size(max = 30, message = "El teléfono no puede superar 30 caracteres")
        String telefono,

        @Email(message = "El email no tiene un formato válido")
        @Size(max = 120, message = "El email no puede superar 120 caracteres")
        String email,

        @Size(max = 200, message = "La dirección no puede superar 200 caracteres")
        String direccion
) {
}
