package com.taller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AgregarDetalleTrabajoRequest(
        @NotBlank @Size(max = 1500) String descripcion,
        @NotNull Long idUsuarioResponsable
) {}
