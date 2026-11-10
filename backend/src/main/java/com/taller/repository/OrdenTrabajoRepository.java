package com.taller.repository;

import com.taller.entity.OrdenTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Integer> {
    List<OrdenTrabajo> findByEstado(String estado);
    // List<OrdenTrabajo> findByVehiculoIdVehiculoOrderByFechaIngresoDesc(Integer idVehiculo);
}