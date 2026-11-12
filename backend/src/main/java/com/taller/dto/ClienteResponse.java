package com.taller.dto;

import java.time.LocalDateTime;

public record ClienteResponse(
        Long idCliente,
        String nombre,
        String dniCuit,
        String telefono,
        String email,
        String direccion,
        LocalDateTime fechaRegistro
) {
}
