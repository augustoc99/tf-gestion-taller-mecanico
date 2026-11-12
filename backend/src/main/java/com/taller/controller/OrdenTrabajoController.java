package com.taller.controller;

import com.taller.dto.*;
import com.taller.entity.EstadoOrden;
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
    public List<OrdenTrabajoResponse> listar(@RequestParam(required = false) EstadoOrden estado) {
        return (estado == null ? ordenService.listar() : ordenService.listarPorEstado(estado))
                .stream()
                .map(OrdenTrabajoResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public OrdenTrabajoResponse obtener(@PathVariable Long id) {
        return OrdenTrabajoResponse.from(ordenService.obtener(id));
    }

    @GetMapping("/vehiculo/{idVehiculo}")
    public List<OrdenTrabajoResponse> listarPorVehiculo(@PathVariable Long idVehiculo) {
        return ordenService.listarPorVehiculo(idVehiculo)
                .stream()
                .map(OrdenTrabajoResponse::from)
                .toList();
    }

    @PostMapping
    public OrdenTrabajoResponse crear(@Valid @RequestBody CrearOrdenRequest req) {
        return OrdenTrabajoResponse.from(ordenService.crear(req));
    }

    @PutMapping("/{id}/ingreso")
    public OrdenTrabajoResponse actualizarIngreso(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarIngresoOrdenRequest req) {
        return OrdenTrabajoResponse.from(ordenService.actualizarIngreso(id, req));
    }

    @PutMapping("/{id}/tecnica")
    public OrdenTrabajoResponse registrarDatosTecnicos(
            @PathVariable Long id,
            @Valid @RequestBody RegistrarDatosTecnicosRequest req) {
        return OrdenTrabajoResponse.from(ordenService.registrarDatosTecnicos(id, req));
    }

    @PutMapping("/{id}/estado")
    public OrdenTrabajoResponse cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambioEstadoRequest req) {
        return OrdenTrabajoResponse.from(ordenService.cambiarEstado(id, req));
    }

    @PutMapping("/{id}/mecanico")
    public OrdenTrabajoResponse asignarMecanico(
            @PathVariable Long id,
            @Valid @RequestBody AsignarMecanicoRequest req) {
        return OrdenTrabajoResponse.from(
                ordenService.asignarMecanico(id, req.idMecanico(), req.idUsuarioResponsable())
        );
    }

    @GetMapping("/{id}/historial")
    public List<HistorialEstadoResponse> historial(@PathVariable Long id) {
        return ordenService.obtenerHistorial(id)
                .stream()
                .map(HistorialEstadoResponse::from)
                .toList();
    }

    @PostMapping("/{id}/trabajos")
    public DetalleTrabajoResponse agregarTrabajo(
            @PathVariable Long id,
            @Valid @RequestBody AgregarDetalleTrabajoRequest req) {
        return DetalleTrabajoResponse.from(detalleService.agregar(id, req));
    }

    @GetMapping("/{id}/trabajos")
    public List<DetalleTrabajoResponse> listarTrabajos(@PathVariable Long id) {
        return detalleService.listarPorOrden(id)
                .stream()
                .map(DetalleTrabajoResponse::from)
                .toList();
    }
}
