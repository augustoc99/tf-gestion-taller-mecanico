package com.taller.dto;

import com.taller.entity.EstadoOrden;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CambioEstadoRequest(
        @NotNull EstadoOrden estadoNuevo,
        @NotNull Long idUsuarioResponsable,
        @Size(max = 500) String observacion
) {}
