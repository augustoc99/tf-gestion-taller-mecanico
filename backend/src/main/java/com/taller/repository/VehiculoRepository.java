package com.taller.repository;

import com.taller.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Optional<Vehiculo> findByPatenteIgnoreCase(String patente);

    boolean existsByPatenteIgnoreCase(String patente);

    boolean existsByPatenteIgnoreCaseAndIdVehiculoNot(String patente, Long idVehiculo);

    @Query("""
            SELECT v
            FROM Vehiculo v
            JOIN FETCH v.cliente c
            WHERE (:idCliente IS NULL OR c.idCliente = :idCliente)
              AND (
                    :criterio IS NULL
                    OR LOWER(v.patente) LIKE LOWER(CONCAT('%', :criterio, '%'))
                    OR LOWER(v.marca) LIKE LOWER(CONCAT('%', :criterio, '%'))
                    OR LOWER(v.modelo) LIKE LOWER(CONCAT('%', :criterio, '%'))
                    OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :criterio, '%'))
              )
            ORDER BY v.patente ASC
            """)
    List<Vehiculo> buscar(
            @Param("idCliente") Long idCliente,
            @Param("criterio") String criterio
    );
}
