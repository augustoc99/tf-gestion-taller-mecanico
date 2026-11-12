package com.taller.dto;

import com.taller.entity.DetalleTrabajo;

import java.time.LocalDateTime;

public record DetalleTrabajoResponse(
        Long idDetalle,
        Long idOrden,
        String descripcion,
        LocalDateTime fechaRegistro
) {
    public static DetalleTrabajoResponse from(DetalleTrabajo detalle) {
        return new DetalleTrabajoResponse(
                detalle.getIdDetalle(),
                detalle.getOrden().getIdOrden(),
                detalle.getDescripcion(),
                detalle.getFechaRegistro()
        );
    }
}
