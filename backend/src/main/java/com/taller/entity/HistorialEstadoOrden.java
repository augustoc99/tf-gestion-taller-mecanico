package com.taller.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "historial_estado_orden")
public class HistorialEstadoOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Long idHistorial;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_orden", nullable = false)
    private OrdenTrabajo orden;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", nullable = false, length = 30)
    private EstadoOrden estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 30)
    private EstadoOrden estadoNuevo;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "observacion", length = 500)
    private String observacion;
}
