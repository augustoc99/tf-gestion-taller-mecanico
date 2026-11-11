package com.taller.controller;

import com.taller.dto.ActualizarUsuarioRequest;
import com.taller.dto.CambiarEstadoUsuarioRequest;
import com.taller.dto.CrearUsuarioRequest;
import com.taller.dto.UsuarioResponse;
import com.taller.entity.RolUsuario;
import com.taller.entity.Usuario;
import com.taller.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponse> listar(@RequestParam(required = false) RolUsuario rol) {
        return usuarioService.listar(rol).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{idUsuario}")
    public UsuarioResponse obtener(@PathVariable Long idUsuario) {
        return toResponse(usuarioService.obtener(idUsuario));
    }

    @GetMapping("/mecanicos")
    public List<UsuarioResponse> listarMecanicosActivos() {
        return usuarioService.listarMecanicosActivos().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/roles")
    public List<RolUsuario> listarRoles() {
        return Arrays.asList(RolUsuario.values());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crear(@Valid @RequestBody CrearUsuarioRequest request) {
        return toResponse(usuarioService.crear(request));
    }

    @PutMapping("/{idUsuario}")
    public UsuarioResponse actualizar(
            @PathVariable Long idUsuario,
            @Valid @RequestBody ActualizarUsuarioRequest request) {
        return toResponse(usuarioService.actualizar(idUsuario, request));
    }

    @PatchMapping("/{idUsuario}/activo")
    public UsuarioResponse cambiarActivo(
            @PathVariable Long idUsuario,
            @Valid @RequestBody CambiarEstadoUsuarioRequest request) {
        return toResponse(usuarioService.cambiarActivo(idUsuario, request.activo()));
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombreCompleto(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getActivo(),
                usuario.getFechaCreacion()
        );
    }
}
