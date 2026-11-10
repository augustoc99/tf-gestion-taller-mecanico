package com.taller.controller;

import com.taller.entity.HistorialEstado;
import com.taller.entity.OrdenTrabajo;
import com.taller.service.OrdenTrabajoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenTrabajoController {

    private final OrdenTrabajoService service;

    public OrdenTrabajoController(OrdenTrabajoService service) {
        this.service = service;
    }

    // DTOs simples como records (Java 17)
    public record CambioEstadoRequest(String estadoNuevo, String observacion) {}
    public record AsignarMecanicoRequest(Integer idMecanico) {}

    @GetMapping
    public List<OrdenTrabajo> listar(@RequestParam(required = false) String estado) {
        if (estado != null && !estado.isEmpty()) {
            return service.listarPorEstado(estado);
        }
        return service.listar();
    }

    @GetMapping("/{id}")
    public OrdenTrabajo obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PostMapping
    public OrdenTrabajo crear(@RequestBody OrdenTrabajo orden) {
        return service.crear(orden);
    }

    @PutMapping("/{id}")
    public OrdenTrabajo actualizar(@PathVariable Integer id, @RequestBody OrdenTrabajo orden) {
        return service.actualizar(id, orden);
    }

    @PutMapping("/{id}/estado")
    public OrdenTrabajo cambiarEstado(@PathVariable Integer id, @RequestBody CambioEstadoRequest req) {
        return service.cambiarEstado(id, req.estadoNuevo(), req.observacion());
    }

    @PutMapping("/{id}/mecanico")
    public OrdenTrabajo asignarMecanico(@PathVariable Integer id, @RequestBody AsignarMecanicoRequest req) {
        return service.asignarMecanico(id, req.idMecanico());
    }

    @GetMapping("/{id}/historial")
    public List<HistorialEstado> historial(@PathVariable Integer id) {
        return service.obtenerHistorial(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}