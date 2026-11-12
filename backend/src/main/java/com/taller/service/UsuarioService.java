package com.taller.service;

import com.taller.dto.ActualizarUsuarioRequest;
import com.taller.dto.CrearUsuarioRequest;
import com.taller.entity.RolUsuario;
import com.taller.entity.Usuario;
import com.taller.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listar(RolUsuario rol) {
        if (rol == null) {
            return usuarioRepository.findAllByOrderByNombreCompletoAsc();
        }
        return usuarioRepository.findByRolOrderByNombreCompletoAsc(rol);
    }

    public List<Usuario> listarMecanicosActivos() {
        return usuarioRepository.findByRolAndActivoTrueOrderByNombreCompletoAsc(RolUsuario.MECANICO);
    }

    public Usuario obtener(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + idUsuario));
    }

    @Transactional
    public Usuario crear(CrearUsuarioRequest request) {
        String email = normalizarEmail(request.email());
        validarEmailDisponible(email, null);

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(normalizarNombre(request.nombreCompleto()));
        usuario.setEmail(email);
        usuario.setRol(request.rol());
        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(Long idUsuario, ActualizarUsuarioRequest request) {
        Usuario usuario = obtener(idUsuario);
        String email = normalizarEmail(request.email());
        validarEmailDisponible(email, idUsuario);

        usuario.setNombreCompleto(normalizarNombre(request.nombreCompleto()));
        usuario.setEmail(email);

        // El rol no se modifica desde este endpoint. Mantenerlo estable evita
        // alterar la interpretación funcional de asignaciones e historial ya existentes.
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario cambiarActivo(Long idUsuario, boolean activo) {
        Usuario usuario = obtener(idUsuario);
        usuario.setActivo(activo);
        return usuarioRepository.save(usuario);
    }

    private void validarEmailDisponible(String email, Long idUsuarioActual) {
        if (email == null) {
            return;
        }

        boolean ocupado = idUsuarioActual == null
                ? usuarioRepository.existsByEmailIgnoreCase(email)
                : usuarioRepository.existsByEmailIgnoreCaseAndIdUsuarioNot(email, idUsuarioActual);

        if (ocupado) {
            throw new RuntimeException("Ya existe un usuario con el email: " + email);
        }
    }

    private String normalizarNombre(String nombre) {
        return nombre.trim().replaceAll("\\s+", " ");
    }

    private String normalizarEmail(String email) {
        if (email == null) {
            return null;
        }
        String limpio = email.trim();
        return limpio.isEmpty() ? null : limpio.toLowerCase(Locale.ROOT);
    }
}
