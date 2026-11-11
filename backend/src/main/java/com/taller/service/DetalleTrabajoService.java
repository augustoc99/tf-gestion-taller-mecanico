package com.taller.service;

import com.taller.dto.AgregarDetalleTrabajoRequest;
import com.taller.entity.DetalleTrabajo;
import com.taller.entity.EstadoOrden;
import com.taller.entity.OrdenTrabajo;
import com.taller.repository.DetalleTrabajoRepository;
import com.taller.repository.OrdenTrabajoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DetalleTrabajoService {

    private final DetalleTrabajoRepository detalleRepo;
    private final OrdenTrabajoRepository ordenRepo;

    public DetalleTrabajoService(DetalleTrabajoRepository detalleRepo, OrdenTrabajoRepository ordenRepo) {
        this.detalleRepo = detalleRepo;
        this.ordenRepo = ordenRepo;
    }

    public List<DetalleTrabajo> listarPorOrden(Long idOrden) {
        verificarOrden(idOrden);
        return detalleRepo.findByOrden_IdOrdenOrderByFechaRegistroAsc(idOrden);
    }

    @Transactional
    public DetalleTrabajo agregar(Long idOrden, AgregarDetalleTrabajoRequest req) {
        OrdenTrabajo orden = verificarOrden(idOrden);
        if (orden.getEstado() == EstadoOrden.ENTREGADO) {
            throw new RuntimeException("No se puede agregar trabajo a una orden ENTREGADA");
        }

        DetalleTrabajo detalle = new DetalleTrabajo();
        detalle.setOrden(orden);
        detalle.setDescripcion(req.descripcion().trim());
        return detalleRepo.save(detalle);
    }

    private OrdenTrabajo verificarOrden(Long idOrden) {
        return ordenRepo.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + idOrden));
    }
}
