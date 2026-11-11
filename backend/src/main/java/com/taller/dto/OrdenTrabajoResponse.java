package com.taller.dto;

import com.taller.entity.EstadoOrden;
import com.taller.entity.OrdenTrabajo;

import java.time.LocalDateTime;

public record OrdenTrabajoResponse(
        Long idOrden,
        Long idVehiculo,
        String patenteVehiculo,
        Long idCliente,
        String nombreCliente,
        Long idMecanico,
        String nombreMecanico,
        LocalDateTime fechaIngreso,
        LocalDateTime fechaEntrega,
        Integer kilometrajeIngreso,
        String motivoIngreso,
        String diagnostico,
        String observaciones,
        EstadoOrden estado
) {
    public static OrdenTrabajoResponse from(OrdenTrabajo orden) {
        Long idMecanico = orden.getMecanico() == null ? null : orden.getMecanico().getIdUsuario();
        String nombreMecanico = orden.getMecanico() == null ? null : orden.getMecanico().getNombreCompleto();

        return new OrdenTrabajoResponse(
                orden.getIdOrden(),
                orden.getVehiculo().getIdVehiculo(),
                orden.getVehiculo().getPatente(),
                orden.getVehiculo().getCliente().getIdCliente(),
                orden.getVehiculo().getCliente().getNombre(),
                idMecanico,
                nombreMecanico,
                orden.getFechaIngreso(),
                orden.getFechaEntrega(),
                orden.getKilometrajeIngreso(),
                orden.getMotivoIngreso(),
                orden.getDiagnostico(),
                orden.getObservaciones(),
                orden.getEstado()
        );
    }
}
