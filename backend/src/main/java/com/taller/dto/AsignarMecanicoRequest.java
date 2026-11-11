package com.taller.dto;

import jakarta.validation.constraints.NotNull;

public record AsignarMecanicoRequest(
        @NotNull Long idMecanico,
        @NotNull Long idUsuarioResponsable
) {}
