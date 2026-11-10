package com.taller.service;

import com.taller.entity.HistorialEstado;
import com.taller.entity.OrdenTrabajo;
import com.taller.repository.HistorialEstadoRepository;
import com.taller.repository.OrdenTrabajoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrdenTrabajoService {

    private final OrdenTrabajoRepository repo;
    private final HistorialEstadoRepository historialRepo;

    // Máquina de estados: desde -> posibles destinos
    private static final Map<String, List<String>> TRANSICIONES = Map.of(
            "RECIBIDO",           List.of("EN_DIAGNOSTICO"),
            "EN_DIAGNOSTICO",     List.of("EN_REPARACION", "ESPERANDO_REPUESTO"),
            "EN_REPARACION",      List.of("ESPERANDO_REPUESTO", "CONTROL_FINAL"),
            "ESPERANDO_REPUESTO", List.of("EN_REPARACION"),
            "CONTROL_FINAL",      List.of("EN_REPARACION", "FINALIZADO"),
            "FINALIZADO",         List.of("ENTREGADO"),
            "ENTREGADO",          List.of()
    );

    public OrdenTrabajoService(OrdenTrabajoRepository repo, HistorialEstadoRepository historialRepo) {
        this.repo = repo;
        this.historialRepo = historialRepo;
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
        OrdenTrabajo guardada = repo.save(orden);

        // Registrar el estado inicial en el historial
        registrarHistorial(guardada, null, "RECIBIDO", "Creación de la orden");
        return guardada;
    }

    public OrdenTrabajo actualizar(Integer id, OrdenTrabajo datos) {
        OrdenTrabajo orden = obtener(id);
        if ("ENTREGADO".equals(orden.getEstado())) {
            throw new RuntimeException("No se puede modificar una orden ENTREGADA");
        }
        orden.setProblemaInformado(datos.getProblemaInformado());
        orden.setKilometrajeIngreso(datos.getKilometrajeIngreso());
        orden.setDiagnostico(datos.getDiagnostico());
        orden.setObservaciones(datos.getObservaciones());
        return repo.save(orden);
    }

    public OrdenTrabajo cambiarEstado(Integer id, String estadoNuevo, String observacion) {
        OrdenTrabajo orden = obtener(id);
        String estadoActual = orden.getEstado();

        List<String> permitidos = TRANSICIONES.getOrDefault(estadoActual, List.of());
        if (!permitidos.contains(estadoNuevo)) {
            throw new RuntimeException(
                    "Transición inválida: " + estadoActual + " -> " + estadoNuevo
            );
        }

        orden.setEstado(estadoNuevo);
        if ("ENTREGADO".equals(estadoNuevo)) {
            orden.setFechaEgreso(LocalDateTime.now());
        }
        OrdenTrabajo guardada = repo.save(orden);

        registrarHistorial(guardada, estadoActual, estadoNuevo, observacion);
        return guardada;
    }

    public OrdenTrabajo asignarMecanico(Integer id, Integer idMecanico) {
        OrdenTrabajo orden = obtener(id);
        if ("ENTREGADO".equals(orden.getEstado())) {
            throw new RuntimeException("No se puede reasignar una orden ENTREGADA");
        }
        orden.setIdMecanico(idMecanico);
        return repo.save(orden);
    }

    public List<HistorialEstado> obtenerHistorial(Integer idOrden) {
        obtener(idOrden);
        return historialRepo.findByOrdenIdOrdenOrderByFechaCambioAsc(idOrden);
    }

    public void eliminar(Integer id) {
        repo.deleteById(id);
    }

    private void registrarHistorial(OrdenTrabajo orden, String anterior, String nuevo, String observacion) {
        HistorialEstado h = new HistorialEstado();
        h.setOrden(orden);
        h.setEstadoAnterior(anterior);
        h.setEstadoNuevo(nuevo);
        h.setFechaCambio(LocalDateTime.now());
        h.setObservacion(observacion);
        historialRepo.save(h);
    }
}