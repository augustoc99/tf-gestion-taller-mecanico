package com.taller.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "historial_estado")
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHistorial;

    @ManyToOne
    @JoinColumn(name = "id_orden", nullable = false)
    private OrdenTrabajo orden;

    private String estadoAnterior;
    private String estadoNuevo;
    private LocalDateTime fechaCambio;

    // @ManyToOne
    // @JoinColumn(name = "id_usuario")
    // private Usuario usuario;

    private String observacion;

}