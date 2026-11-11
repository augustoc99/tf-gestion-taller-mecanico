package com.taller.repository;

import com.taller.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    Optional<Vehiculo> findByPatenteIgnoreCase(String patente);
    List<Vehiculo> findByCliente_IdClienteOrderByPatenteAsc(Long idCliente);
}
