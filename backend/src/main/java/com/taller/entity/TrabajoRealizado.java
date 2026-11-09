package com.taller.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public Integer getIdTrabajo() { return idTrabajo; }
    public void setIdTrabajo(Integer idTrabajo) { this.idTrabajo = idTrabajo; }
    public OrdenTrabajo getOrden() { return orden; }
    public void setOrden(OrdenTrabajo orden) { this.orden = orden; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getManoObraHoras() { return manoObraHoras; }
    public void setManoObraHoras(BigDecimal manoObraHoras) { this.manoObraHoras = manoObraHoras; }
    public String getRepuestosUtilizados() { return repuestosUtilizados; }
    public void setRepuestosUtilizados(String repuestosUtilizados) { this.repuestosUtilizados = repuestosUtilizados; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}