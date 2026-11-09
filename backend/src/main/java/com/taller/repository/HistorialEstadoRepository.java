package com.taller.repository;

import com.taller.entity.HistorialEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Integer> {
    List<HistorialEstado> findByOrdenIdOrdenOrderByFechaCambioAsc(Integer idOrden);
}