package com.taller.repository;

import com.taller.entity.HistorialEstadoOrden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialEstadoOrdenRepository extends JpaRepository<HistorialEstadoOrden, Long> {
    List<HistorialEstadoOrden> findByOrden_IdOrdenOrderByFechaCambioAsc(Long idOrden);
}
