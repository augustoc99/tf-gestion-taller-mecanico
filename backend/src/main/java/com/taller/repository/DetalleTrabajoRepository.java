package com.taller.repository;

import com.taller.entity.DetalleTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleTrabajoRepository extends JpaRepository<DetalleTrabajo, Long> {
    List<DetalleTrabajo> findByOrden_IdOrdenOrderByFechaRegistroAsc(Long idOrden);
}
