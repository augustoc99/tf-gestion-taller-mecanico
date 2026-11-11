package com.taller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarDatosTecnicosRequest(
        @NotBlank @Size(max = 2000) String diagnostico,
        @Size(max = 2000) String observaciones,
        @NotNull Long idUsuarioResponsable
) {}
