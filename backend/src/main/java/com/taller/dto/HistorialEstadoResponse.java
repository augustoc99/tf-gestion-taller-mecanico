package com.taller.dto;

import com.taller.entity.EstadoOrden;
import com.taller.entity.HistorialEstadoOrden;
import com.taller.entity.RolUsuario;

import java.time.LocalDateTime;

public record HistorialEstadoResponse(
        Long idHistorial,
        Long idOrden,
        EstadoOrden estadoAnterior,
        EstadoOrden estadoNuevo,
        LocalDateTime fechaCambio,
        Long idUsuario,
        String nombreUsuario,
        RolUsuario rolUsuario,
        String observacion
) {
    public static HistorialEstadoResponse from(HistorialEstadoOrden historial) {
        return new HistorialEstadoResponse(
                historial.getIdHistorial(),
                historial.getOrden().getIdOrden(),
                historial.getEstadoAnterior(),
                historial.getEstadoNuevo(),
                historial.getFechaCambio(),
                historial.getUsuario().getIdUsuario(),
                historial.getUsuario().getNombreCompleto(),
                historial.getUsuario().getRol(),
                historial.getObservacion()
        );
    }
}
