package com.taller.repository;

import com.taller.entity.RolUsuario;
import com.taller.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdUsuarioNot(String email, Long idUsuario);

    List<Usuario> findAllByOrderByNombreCompletoAsc();

    List<Usuario> findByRolOrderByNombreCompletoAsc(RolUsuario rol);

    List<Usuario> findByRolAndActivoTrueOrderByNombreCompletoAsc(RolUsuario rol);
}
