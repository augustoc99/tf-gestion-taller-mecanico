package com.taller.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "trabajo_realizado")
public class TrabajoRealizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTrabajo;

    @ManyToOne
    @JoinColumn(name = "id_orden", nullable = false)
    private OrdenTrabajo orden;

    private String descripcion;
    private BigDecimal manoObraHoras;
    private String repuestosUtilizados;
    private LocalDateTime fechaRegistro;

}