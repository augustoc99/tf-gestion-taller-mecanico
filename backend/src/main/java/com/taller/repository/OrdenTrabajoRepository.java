package com.taller.repository;

import com.taller.entity.EstadoOrden;
import com.taller.entity.OrdenTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {
    List<OrdenTrabajo> findByEstadoOrderByFechaIngresoDesc(EstadoOrden estado);
    List<OrdenTrabajo> findByVehiculo_IdVehiculoOrderByFechaIngresoDesc(Long idVehiculo);
}
