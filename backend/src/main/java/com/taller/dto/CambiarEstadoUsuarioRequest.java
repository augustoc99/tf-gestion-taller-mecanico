package com.taller.dto;

import jakarta.validation.constraints.NotNull;

public record CambiarEstadoUsuarioRequest(
        @NotNull(message = "El estado activo es obligatorio")
        Boolean activo
) {
}
