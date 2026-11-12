package com.taller.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long idVehiculo;

    @Column(name = "patente", nullable = false, unique = true, length = 15)
    private String patente;

    @Column(name = "marca", nullable = false, length = 60)
    private String marca;

    @Column(name = "modelo", nullable = false, length = 60)
    private String modelo;

    @Column(name = "anio")
    private Integer anio;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;
}
