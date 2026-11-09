package com.taller.service;

import com.taller.entity.OrdenTrabajo;
import com.taller.repository.OrdenTrabajoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdenTrabajoService {

    private final OrdenTrabajoRepository repo;

    public OrdenTrabajoService(OrdenTrabajoRepository repo) {
        this.repo = repo;
    }

    public List<OrdenTrabajo> listar() {
        return repo.findAll();
    }

    public List<OrdenTrabajo> listarPorEstado(String estado) {
        return repo.findByEstado(estado);
    }

    public OrdenTrabajo obtener(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + id));
    }

    public OrdenTrabajo crear(OrdenTrabajo orden) {
        orden.setFechaIngreso(LocalDateTime.now());
        if (orden.getEstado() == null) {
            orden.setEstado("RECIBIDO");
        }
        return repo.save(orden);
    }

    public OrdenTrabajo actualizar(Integer id, OrdenTrabajo datos) {
        OrdenTrabajo orden = obtener(id);
        orden.setProblemaInformado(datos.getProblemaInformado());
        orden.setKilometrajeIngreso(datos.getKilometrajeIngreso());
        orden.setDiagnostico(datos.getDiagnostico());
        orden.setObservaciones(datos.getObservaciones());
        return repo.save(orden);
    }

    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}