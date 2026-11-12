package com.taller.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "orden_trabajo")
public class OrdenTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden")
    private Long idOrden;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_mecanico")
    private Usuario mecanico;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "kilometraje_ingreso", nullable = false)
    private Integer kilometrajeIngreso;

    @Column(name = "motivo_ingreso", nullable = false, length = 1000)
    private String motivoIngreso;

    @Column(name = "diagnostico", length = 2000)
    private String diagnostico;

    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoOrden estado = EstadoOrden.RECIBIDO;
}
