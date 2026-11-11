package com.taller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarUsuarioRequest(
        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 120, message = "El nombre completo no puede superar 120 caracteres")
        String nombreCompleto,

        @Email(message = "El email no tiene un formato válido")
        @Size(max = 120, message = "El email no puede superar 120 caracteres")
        String email
) {
}
