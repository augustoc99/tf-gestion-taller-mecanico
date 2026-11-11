package com.taller.service;

import com.taller.dto.ActualizarIngresoOrdenRequest;
import com.taller.dto.CambioEstadoRequest;
import com.taller.dto.CrearOrdenRequest;
import com.taller.dto.RegistrarDatosTecnicosRequest;
import com.taller.entity.*;
import com.taller.repository.HistorialEstadoOrdenRepository;
import com.taller.repository.OrdenTrabajoRepository;
import com.taller.repository.UsuarioRepository;
import com.taller.repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OrdenTrabajoService {

    private static final Map<EstadoOrden, List<EstadoOrden>> TRANSICIONES = Map.of(
            EstadoOrden.RECIBIDO, List.of(EstadoOrden.EN_DIAGNOSTICO),
            EstadoOrden.EN_DIAGNOSTICO, List.of(EstadoOrden.EN_REPARACION, EstadoOrden.ESPERANDO_REPUESTO),
            EstadoOrden.EN_REPARACION, List.of(EstadoOrden.ESPERANDO_REPUESTO, EstadoOrden.CONTROL_FINAL),
            EstadoOrden.ESPERANDO_REPUESTO, List.of(EstadoOrden.EN_REPARACION),
            EstadoOrden.CONTROL_FINAL, List.of(EstadoOrden.EN_REPARACION, EstadoOrden.FINALIZADO),
            EstadoOrden.FINALIZADO, List.of(EstadoOrden.ENTREGADO),
            EstadoOrden.ENTREGADO, List.of()
    );

    private static final Set<EstadoOrden> ESTADOS_DATOS_TECNICOS = EnumSet.of(
            EstadoOrden.EN_DIAGNOSTICO,
            EstadoOrden.EN_REPARACION,
            EstadoOrden.ESPERANDO_REPUESTO,
            EstadoOrden.CONTROL_FINAL
    );

    private final OrdenTrabajoRepository ordenRepo;
    private final HistorialEstadoOrdenRepository historialRepo;
    private final VehiculoRepository vehiculoRepo;
    private final UsuarioRepository usuarioRepo;

    public OrdenTrabajoService(
            OrdenTrabajoRepository ordenRepo,
            HistorialEstadoOrdenRepository historialRepo,
            VehiculoRepository vehiculoRepo,
            UsuarioRepository usuarioRepo) {
        this.ordenRepo = ordenRepo;
        this.historialRepo = historialRepo;
        this.vehiculoRepo = vehiculoRepo;
        this.usuarioRepo = usuarioRepo;
    }

    public List<OrdenTrabajo> listar() {
        return ordenRepo.findAll();
    }

    public List<OrdenTrabajo> listarPorEstado(EstadoOrden estado) {
        return ordenRepo.findByEstadoOrderByFechaIngresoDesc(estado);
    }

    public List<OrdenTrabajo> listarPorVehiculo(Long idVehiculo) {
        if (!vehiculoRepo.existsById(idVehiculo)) {
            throw new RuntimeException("Vehículo no encontrado: " + idVehiculo);
        }
        return ordenRepo.findByVehiculo_IdVehiculoOrderByFechaIngresoDesc(idVehiculo);
    }

    public OrdenTrabajo obtener(Long id) {
        return ordenRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + id));
    }

    @Transactional
    public OrdenTrabajo crear(CrearOrdenRequest req) {
        obtenerRecepcionActiva(req.idUsuarioResponsable());

        Vehiculo vehiculo = vehiculoRepo.findById(req.idVehiculo())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado: " + req.idVehiculo()));

        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setVehiculo(vehiculo);
        orden.setKilometrajeIngreso(req.kilometrajeIngreso());
        orden.setMotivoIngreso(req.motivoIngreso().trim());
        orden.setObservaciones(limpiar(req.observaciones()));
        orden.setFechaIngreso(LocalDateTime.now());
        orden.setEstado(EstadoOrden.RECIBIDO);

        if (req.idMecanico() != null) {
            orden.setMecanico(obtenerMecanicoValido(req.idMecanico()));
        }

        // RECIBIDO es el estado inicial, no una transición: no genera historial.
        return ordenRepo.save(orden);
    }

    @Transactional
    public OrdenTrabajo actualizarIngreso(Long id, ActualizarIngresoOrdenRequest req) {
        obtenerRecepcionActiva(req.idUsuarioResponsable());

        OrdenTrabajo orden = obtener(id);
        if (orden.getEstado() != EstadoOrden.RECIBIDO) {
            throw new RuntimeException("No se puede modificar el ingreso una vez iniciada la etapa técnica");
        }

        orden.setKilometrajeIngreso(req.kilometrajeIngreso());
        orden.setMotivoIngreso(req.motivoIngreso().trim());
        orden.setObservaciones(limpiar(req.observaciones()));
        return ordenRepo.save(orden);
    }

    @Transactional
    public OrdenTrabajo registrarDatosTecnicos(Long id, RegistrarDatosTecnicosRequest req) {
        OrdenTrabajo orden = obtener(id);
        Usuario responsable = obtenerUsuarioActivo(req.idUsuarioResponsable());
        validarMecanicoAsignado(orden, responsable);

        if (!ESTADOS_DATOS_TECNICOS.contains(orden.getEstado())) {
            throw new RuntimeException("No se pueden registrar datos técnicos en el estado " + orden.getEstado());
        }

        orden.setDiagnostico(req.diagnostico().trim());
        orden.setObservaciones(limpiar(req.observaciones()));
        return ordenRepo.save(orden);
    }

    @Transactional
    public OrdenTrabajo cambiarEstado(Long id, CambioEstadoRequest req) {
        OrdenTrabajo orden = obtener(id);
        EstadoOrden actual = orden.getEstado();
        EstadoOrden nuevo = req.estadoNuevo();

        List<EstadoOrden> destinos = TRANSICIONES.getOrDefault(actual, List.of());
        if (!destinos.contains(nuevo)) {
            throw new RuntimeException("Transición inválida: " + actual + " -> " + nuevo);
        }

        Usuario responsable = obtenerUsuarioActivo(req.idUsuarioResponsable());
        validarResponsableTransicion(orden, nuevo, responsable);

        orden.setEstado(nuevo);
        if (nuevo == EstadoOrden.ENTREGADO) {
            orden.setFechaEntrega(LocalDateTime.now());
        }
        OrdenTrabajo guardada = ordenRepo.save(orden);

        registrarHistorial(guardada, actual, nuevo, responsable, req.observacion());
        return guardada;
    }

    @Transactional
    public OrdenTrabajo asignarMecanico(Long idOrden, Long idMecanico, Long idUsuarioResponsable) {
        obtenerRecepcionActiva(idUsuarioResponsable);

        OrdenTrabajo orden = obtener(idOrden);
        if (orden.getEstado() == EstadoOrden.FINALIZADO || orden.getEstado() == EstadoOrden.ENTREGADO) {
            throw new RuntimeException("No se puede asignar o reasignar un mecánico a una orden cerrada");
        }

        orden.setMecanico(obtenerMecanicoValido(idMecanico));
        return ordenRepo.save(orden);
    }

    public List<HistorialEstadoOrden> obtenerHistorial(Long idOrden) {
        obtener(idOrden);
        return historialRepo.findByOrden_IdOrdenOrderByFechaCambioAsc(idOrden);
    }

    private Usuario obtenerUsuarioActivo(Long idUsuario) {
        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + idUsuario));
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new RuntimeException("No se puede operar con un usuario inactivo");
        }
        return usuario;
    }

    private Usuario obtenerRecepcionActiva(Long idUsuario) {
        Usuario usuario = obtenerUsuarioActivo(idUsuario);
        if (usuario.getRol() != RolUsuario.RECEPCION_ADMINISTRACION) {
            throw new RuntimeException("Sólo Recepción/Administración puede realizar esta operación");
        }
        return usuario;
    }

    private Usuario obtenerMecanicoValido(Long idMecanico) {
        Usuario mecanico = obtenerUsuarioActivo(idMecanico);
        if (mecanico.getRol() != RolUsuario.MECANICO) {
            throw new RuntimeException("El usuario asignado debe tener rol MECANICO");
        }
        return mecanico;
    }

    private void validarMecanicoAsignado(OrdenTrabajo orden, Usuario responsable) {
        if (responsable.getRol() != RolUsuario.MECANICO) {
            throw new RuntimeException("Los datos técnicos debe registrarlos un usuario MECANICO");
        }
        if (orden.getMecanico() == null) {
            throw new RuntimeException("Debe asignarse un mecánico antes de realizar operaciones técnicas");
        }
        if (!orden.getMecanico().getIdUsuario().equals(responsable.getIdUsuario())) {
            throw new RuntimeException("Sólo el mecánico asignado puede realizar operaciones técnicas sobre la orden");
        }
    }

    private void validarResponsableTransicion(OrdenTrabajo orden, EstadoOrden nuevo, Usuario responsable) {
        if (nuevo == EstadoOrden.ENTREGADO) {
            if (responsable.getRol() != RolUsuario.RECEPCION_ADMINISTRACION) {
                throw new RuntimeException("La entrega debe registrarla Recepción/Administración");
            }
            return;
        }

        validarMecanicoAsignado(orden, responsable);
    }

    private void registrarHistorial(
            OrdenTrabajo orden,
            EstadoOrden anterior,
            EstadoOrden nuevo,
            Usuario responsable,
            String observacion) {

        HistorialEstadoOrden historial = new HistorialEstadoOrden();
        historial.setOrden(orden);
        historial.setEstadoAnterior(anterior);
        historial.setEstadoNuevo(nuevo);
        historial.setFechaCambio(LocalDateTime.now());
        historial.setUsuario(responsable);
        historial.setObservacion(limpiar(observacion));
        historialRepo.save(historial);
    }

    private String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        String limpio = valor.trim();
        return limpio.isEmpty() ? null : limpio;
    }
}
