package com.taller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CrearOrdenRequest(
        @NotNull Long idVehiculo,
        Long idMecanico,
        @NotNull @PositiveOrZero Integer kilometrajeIngreso,
        @NotBlank @Size(max = 1000) String motivoIngreso,
        @Size(max = 2000) String observaciones,
        @NotNull Long idUsuarioResponsable
) {}
