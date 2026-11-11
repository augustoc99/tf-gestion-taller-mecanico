package com.taller.service;

import com.taller.dto.ActualizarUsuarioRequest;
import com.taller.dto.CrearUsuarioRequest;
import com.taller.entity.RolUsuario;
import com.taller.entity.Usuario;
import com.taller.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void limpiar() {
        usuarioRepository.deleteAll();
    }

    @Test
    void creaUsuarioNormalizandoNombreEmailYActivo() {
        Usuario creado = usuarioService.crear(new CrearUsuarioRequest(
                "  Juan   Pérez  ",
                "  JUAN@EJEMPLO.COM ",
                RolUsuario.MECANICO
        ));

        assertNotNull(creado.getIdUsuario());
        assertEquals("Juan Pérez", creado.getNombreCompleto());
        assertEquals("juan@ejemplo.com", creado.getEmail());
        assertEquals(RolUsuario.MECANICO, creado.getRol());
        assertTrue(creado.getActivo());
        assertNotNull(creado.getFechaCreacion());
    }

    @Test
    void permiteEmailNulo() {
        Usuario creado = usuarioService.crear(new CrearUsuarioRequest(
                "Ana López",
                "   ",
                RolUsuario.RECEPCION_ADMINISTRACION
        ));

        assertNull(creado.getEmail());
    }

    @Test
    void rechazaEmailDuplicadoSinDistinguirMayusculas() {
        usuarioService.crear(new CrearUsuarioRequest(
                "Primer Usuario",
                "usuario@ejemplo.com",
                RolUsuario.MECANICO
        ));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                usuarioService.crear(new CrearUsuarioRequest(
                        "Segundo Usuario",
                        "USUARIO@EJEMPLO.COM",
                        RolUsuario.RECEPCION_ADMINISTRACION
                ))
        );

        assertTrue(ex.getMessage().contains("Ya existe"));
    }

    @Test
    void actualizaDatosSinModificarRol() {
        Usuario creado = usuarioService.crear(new CrearUsuarioRequest(
                "Nombre Inicial",
                "inicial@ejemplo.com",
                RolUsuario.MECANICO
        ));

        Usuario actualizado = usuarioService.actualizar(
                creado.getIdUsuario(),
                new ActualizarUsuarioRequest("Nombre Actualizado", "nuevo@ejemplo.com")
        );

        assertEquals("Nombre Actualizado", actualizado.getNombreCompleto());
        assertEquals("nuevo@ejemplo.com", actualizado.getEmail());
        assertEquals(RolUsuario.MECANICO, actualizado.getRol());
    }

    @Test
    void listaSoloMecanicosActivos() {
        Usuario activo = usuarioService.crear(new CrearUsuarioRequest(
                "Mecánico Activo", null, RolUsuario.MECANICO));
        Usuario inactivo = usuarioService.crear(new CrearUsuarioRequest(
                "Mecánico Inactivo", null, RolUsuario.MECANICO));
        usuarioService.crear(new CrearUsuarioRequest(
                "Recepción", null, RolUsuario.RECEPCION_ADMINISTRACION));

        usuarioService.cambiarActivo(inactivo.getIdUsuario(), false);

        List<Usuario> mecanicos = usuarioService.listarMecanicosActivos();

        assertEquals(1, mecanicos.size());
        assertEquals(activo.getIdUsuario(), mecanicos.get(0).getIdUsuario());
    }

    @Test
    void puedeDesactivarYReactivarUsuario() {
        Usuario creado = usuarioService.crear(new CrearUsuarioRequest(
                "Operador", null, RolUsuario.RECEPCION_ADMINISTRACION));

        assertFalse(usuarioService.cambiarActivo(creado.getIdUsuario(), false).getActivo());
        assertTrue(usuarioService.cambiarActivo(creado.getIdUsuario(), true).getActivo());
    }
}
