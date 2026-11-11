package com.taller.dto;

import com.taller.entity.RolUsuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long idUsuario,
        String nombreCompleto,
        String email,
        RolUsuario rol,
        Boolean activo,
        LocalDateTime fechaCreacion
) {
}
