package com.taller.service;

import com.taller.entity.OrdenTrabajo;
import com.taller.entity.TrabajoRealizado;
import com.taller.repository.OrdenTrabajoRepository;
import com.taller.repository.TrabajoRealizadoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrabajoRealizadoService {

    private final TrabajoRealizadoRepository repo;
    private final OrdenTrabajoRepository ordenRepo;

    public TrabajoRealizadoService(TrabajoRealizadoRepository repo, OrdenTrabajoRepository ordenRepo) {
        this.repo = repo;
        this.ordenRepo = ordenRepo;
    }

    public List<TrabajoRealizado> listarPorOrden(Integer idOrden) {
        return repo.findByOrdenIdOrden(idOrden);
    }

    public TrabajoRealizado agregar(Integer idOrden, TrabajoRealizado trabajo) {
        OrdenTrabajo orden = ordenRepo.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + idOrden));
        trabajo.setOrden(orden);
        trabajo.setFechaRegistro(LocalDateTime.now());
        return repo.save(trabajo);
    }

    public void eliminar(Integer idTrabajo) {
        repo.deleteById(idTrabajo);
    }
}