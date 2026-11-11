package com.taller.repository;

import com.taller.entity.RolUsuario;
import com.taller.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    List<Usuario> findByRolAndActivoTrueOrderByNombreCompletoAsc(RolUsuario rol);
}
