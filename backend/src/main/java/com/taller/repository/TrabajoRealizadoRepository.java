package com.taller.repository;

import com.taller.entity.TrabajoRealizado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrabajoRealizadoRepository extends JpaRepository<TrabajoRealizado, Integer> {
    List<TrabajoRealizado> findByOrdenIdOrden(Integer idOrden);
}