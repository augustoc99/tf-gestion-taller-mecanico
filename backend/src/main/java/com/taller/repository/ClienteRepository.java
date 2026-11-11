package com.taller.repository;

import com.taller.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByDniCuit(String dniCuit);

    boolean existsByDniCuitIgnoreCase(String dniCuit);

    boolean existsByDniCuitIgnoreCaseAndIdClienteNot(String dniCuit, Long idCliente);

    List<Cliente> findAllByOrderByNombreAsc();

    List<Cliente> findByNombreContainingIgnoreCaseOrDniCuitContainingIgnoreCaseOrderByNombreAsc(
            String nombre,
            String dniCuit
    );
}
