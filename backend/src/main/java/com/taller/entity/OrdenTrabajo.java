package com.taller.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "orden_trabajo")
public class OrdenTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOrden;

    // @ManyToOne
    // @JoinColumn(name = "id_vehiculo", nullable = false)
    // private Vehiculo vehiculo;
    @Column(name = "id_vehiculo")  // Solución temporal
    private Integer idVehiculo;

    // @ManyToOne
    // @JoinColumn(name = "id_mecanico")
    // private Usuario mecanico;
    @Column(name = "id_mecanico")  // Solución temporal
    private Integer idMecanico;

    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaEgreso;
    private Integer kilometrajeIngreso;
    private String problemaInformado;
    private String diagnostico;
    private String estado;
    private String observaciones;

}