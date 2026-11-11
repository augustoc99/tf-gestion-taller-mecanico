package com.taller.controller;

import com.taller.dto.*;
import com.taller.entity.DetalleTrabajo;
import com.taller.entity.EstadoOrden;
import com.taller.entity.HistorialEstadoOrden;
import com.taller.entity.OrdenTrabajo;
import com.taller.service.DetalleTrabajoService;
import com.taller.service.OrdenTrabajoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenTrabajoController {

    private final OrdenTrabajoService ordenService;
    private final DetalleTrabajoService detalleService;

    public OrdenTrabajoController(OrdenTrabajoService ordenService, DetalleTrabajoService detalleService) {
        this.ordenService = ordenService;
        this.detalleService = detalleService;
    }

    @GetMapping
    public List<OrdenTrabajo> listar(@RequestParam(required = false) EstadoOrden estado) {
        return estado == null ? ordenService.listar() : ordenService.listarPorEstado(estado);
    }

    @GetMapping("/{id}")
    public OrdenTrabajo obtener(@PathVariable Long id) {
        return ordenService.obtener(id);
    }

    @PostMapping
    public OrdenTrabajo crear(@Valid @RequestBody CrearOrdenRequest req) {
        return ordenService.crear(req);
    }

    @PutMapping("/{id}")
    public OrdenTrabajo actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarOrdenRequest req) {
        return ordenService.actualizar(id, req);
    }

    @PutMapping("/{id}/estado")
    public OrdenTrabajo cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest req) {
        return ordenService.cambiarEstado(id, req);
    }

    @PutMapping("/{id}/mecanico")
    public OrdenTrabajo asignarMecanico(@PathVariable Long id, @Valid @RequestBody AsignarMecanicoRequest req) {
        return ordenService.asignarMecanico(id, req.idMecanico());
    }

    @GetMapping("/{id}/historial")
    public List<HistorialEstadoOrden> historial(@PathVariable Long id) {
        return ordenService.obtenerHistorial(id);
    }

    @PostMapping("/{id}/trabajos")
    public DetalleTrabajo agregarTrabajo(
            @PathVariable Long id,
            @Valid @RequestBody AgregarDetalleTrabajoRequest req) {
        return detalleService.agregar(id, req);
    }

    @GetMapping("/{id}/trabajos")
    public List<DetalleTrabajo> listarTrabajos(@PathVariable Long id) {
        return detalleService.listarPorOrden(id);
    }
}
