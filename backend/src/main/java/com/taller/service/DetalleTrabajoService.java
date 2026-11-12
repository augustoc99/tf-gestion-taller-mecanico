package com.taller.service;

import com.taller.dto.AgregarDetalleTrabajoRequest;
import com.taller.entity.DetalleTrabajo;
import com.taller.entity.EstadoOrden;
import com.taller.entity.OrdenTrabajo;
import com.taller.entity.RolUsuario;
import com.taller.entity.Usuario;
import com.taller.repository.DetalleTrabajoRepository;
import com.taller.repository.OrdenTrabajoRepository;
import com.taller.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class DetalleTrabajoService {

    private static final Set<EstadoOrden> ESTADOS_PERMITIDOS = EnumSet.of(
            EstadoOrden.EN_REPARACION,
            EstadoOrden.CONTROL_FINAL
    );

    private final DetalleTrabajoRepository detalleRepo;
    private final OrdenTrabajoRepository ordenRepo;
    private final UsuarioRepository usuarioRepo;

    public DetalleTrabajoService(
            DetalleTrabajoRepository detalleRepo,
            OrdenTrabajoRepository ordenRepo,
            UsuarioRepository usuarioRepo) {
        this.detalleRepo = detalleRepo;
        this.ordenRepo = ordenRepo;
        this.usuarioRepo = usuarioRepo;
    }

    public List<DetalleTrabajo> listarPorOrden(Long idOrden) {
        verificarOrden(idOrden);
        return detalleRepo.findByOrden_IdOrdenOrderByFechaRegistroAsc(idOrden);
    }

    @Transactional
    public DetalleTrabajo agregar(Long idOrden, AgregarDetalleTrabajoRequest req) {
        OrdenTrabajo orden = verificarOrden(idOrden);
        Usuario responsable = usuarioRepo.findById(req.idUsuarioResponsable())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + req.idUsuarioResponsable()));

        if (!Boolean.TRUE.equals(responsable.getActivo())) {
            throw new RuntimeException("No se puede operar con un usuario inactivo");
        }
        if (responsable.getRol() != RolUsuario.MECANICO) {
            throw new RuntimeException("Los trabajos realizados debe registrarlos un usuario MECANICO");
        }
        if (orden.getMecanico() == null
                || !orden.getMecanico().getIdUsuario().equals(responsable.getIdUsuario())) {
            throw new RuntimeException("Sólo el mecánico asignado puede registrar trabajos en la orden");
        }
        if (!ESTADOS_PERMITIDOS.contains(orden.getEstado())) {
            throw new RuntimeException("No se pueden registrar trabajos en el estado " + orden.getEstado());
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
