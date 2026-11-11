package com.taller.dto;

public record VehiculoResponse(
        Long idVehiculo,
        String patente,
        String marca,
        String modelo,
        Integer anio,
        Long idCliente,
        String nombreCliente,
        String dniCuitCliente
) {
}
