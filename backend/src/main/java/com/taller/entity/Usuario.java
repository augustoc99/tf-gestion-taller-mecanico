package com.taller.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre_completo", nullable = false, length = 120)
    private String nombreCompleto;

    @Column(name = "email", unique = true, length = 120)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 40)
    private RolUsuario rol;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }
}
