package com.taller.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orden_trabajo")
public class OrdenTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOrden;

    // @ManyToOne
    // @JoinColumn(name = "id_vehiculo", nullable = false)
    // private Vehiculo vehiculo;

    // @ManyToOne
    // @JoinColumn(name = "id_mecanico")
    // private Usuario mecanico;

    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaEgreso;
    private Integer kilometrajeIngreso;
    private String problemaInformado;
    private String diagnostico;
    private String estado;
    private String observaciones;

    public Integer getIdOrden() { return idOrden; }
    public void setIdOrden(Integer idOrden) { this.idOrden = idOrden; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDateTime fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public LocalDateTime getFechaEgreso() { return fechaEgreso; }
    public void setFechaEgreso(LocalDateTime fechaEgreso) { this.fechaEgreso = fechaEgreso; }
    public Integer getKilometrajeIngreso() { return kilometrajeIngreso; }
    public void setKilometrajeIngreso(Integer kilometrajeIngreso) { this.kilometrajeIngreso = kilometrajeIngreso; }
    public String getProblemaInformado() { return problemaInformado; }
    public void setProblemaInformado(String problemaInformado) { this.problemaInformado = problemaInformado; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}