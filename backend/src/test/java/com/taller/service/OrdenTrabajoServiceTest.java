package com.taller.service;

import com.taller.dto.*;
import com.taller.entity.*;
import com.taller.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrdenTrabajoServiceTest {

    @Autowired
    private OrdenTrabajoService ordenService;

    @Autowired
    private DetalleTrabajoService detalleService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private HistorialEstadoOrdenRepository historialRepository;

    @Test
    void recepcionCreaOrdenEnRecibidoSinGenerarHistorial() {
        Usuario recepcion = crearUsuario("Recepción", RolUsuario.RECEPCION_ADMINISTRACION);
        Usuario mecanico = crearUsuario("Mecánico", RolUsuario.MECANICO);
        Vehiculo vehiculo = crearVehiculo();

        OrdenTrabajo orden = crearOrden(recepcion, mecanico, vehiculo);

        assertNotNull(orden.getIdOrden());
        assertEquals(EstadoOrden.RECIBIDO, orden.getEstado());
        assertEquals(vehiculo.getIdVehiculo(), orden.getVehiculo().getIdVehiculo());
        assertEquals(mecanico.getIdUsuario(), orden.getMecanico().getIdUsuario());
        assertTrue(historialRepository
                .findByOrden_IdOrdenOrderByFechaCambioAsc(orden.getIdOrden())
                .isEmpty());
    }

    @Test
    void mecanicoNoPuedeCrearOrden() {
        Usuario mecanico = crearUsuario("Mecánico", RolUsuario.MECANICO);
        Vehiculo vehiculo = crearVehiculo();

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ordenService.crear(new CrearOrdenRequest(
                        vehiculo.getIdVehiculo(),
                        mecanico.getIdUsuario(),
                        1000,
                        "Ruido",
                        null,
                        mecanico.getIdUsuario()
                ))
        );

        assertTrue(ex.getMessage().contains("Sólo Recepción"));
    }

    @Test
    void noPuedeIniciarDiagnosticoSinMecanicoAsignado() {
        Usuario recepcion = crearUsuario("Recepción", RolUsuario.RECEPCION_ADMINISTRACION);
        Usuario mecanico = crearUsuario("Mecánico", RolUsuario.MECANICO);
        Vehiculo vehiculo = crearVehiculo();

        OrdenTrabajo orden = ordenService.crear(new CrearOrdenRequest(
                vehiculo.getIdVehiculo(),
                null,
                1000,
                "Ruido",
                null,
                recepcion.getIdUsuario()
        ));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ordenService.cambiarEstado(
                        orden.getIdOrden(),
                        new CambioEstadoRequest(
                                EstadoOrden.EN_DIAGNOSTICO,
                                mecanico.getIdUsuario(),
                                null
                        )
                )
        );

        assertTrue(ex.getMessage().contains("Debe asignarse un mecánico"));
    }

    @Test
    void recepcionPuedeAsignarMecanicoYUnMecanicoNoPuedeAsignarseSolo() {
        Usuario recepcion = crearUsuario("Recepción", RolUsuario.RECEPCION_ADMINISTRACION);
        Usuario mecanico = crearUsuario("Mecánico", RolUsuario.MECANICO);
        Vehiculo vehiculo = crearVehiculo();

        OrdenTrabajo orden = ordenService.crear(new CrearOrdenRequest(
                vehiculo.getIdVehiculo(), null, 1000, "Ruido", null, recepcion.getIdUsuario()));

        orden = ordenService.asignarMecanico(
                orden.getIdOrden(), mecanico.getIdUsuario(), recepcion.getIdUsuario());

        assertEquals(mecanico.getIdUsuario(), orden.getMecanico().getIdUsuario());

        Long idOrden = orden.getIdOrden();
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ordenService.asignarMecanico(idOrden, mecanico.getIdUsuario(), mecanico.getIdUsuario())
        );
        assertTrue(ex.getMessage().contains("Sólo Recepción"));
    }

    @Test
    void completaFlujoNormalConDiagnosticoTrabajoHistorialYEntrega() {
        Usuario recepcion = crearUsuario("Recepción", RolUsuario.RECEPCION_ADMINISTRACION);
        Usuario mecanico = crearUsuario("Mecánico", RolUsuario.MECANICO);
        Vehiculo vehiculo = crearVehiculo();
        OrdenTrabajo orden = crearOrden(recepcion, mecanico, vehiculo);

        ordenService.cambiarEstado(
                orden.getIdOrden(),
                new CambioEstadoRequest(EstadoOrden.EN_DIAGNOSTICO, mecanico.getIdUsuario(), "Inicio diagnóstico"));

        ordenService.registrarDatosTecnicos(
                orden.getIdOrden(),
                new RegistrarDatosTecnicosRequest(
                        "Falla en sistema de frenos",
                        "Revisar pastillas delanteras",
                        mecanico.getIdUsuario()
                ));

        ordenService.cambiarEstado(
                orden.getIdOrden(),
                new CambioEstadoRequest(EstadoOrden.EN_REPARACION, mecanico.getIdUsuario(), null));

        DetalleTrabajo detalle = detalleService.agregar(
                orden.getIdOrden(),
                new AgregarDetalleTrabajoRequest(
                        "Cambio de pastillas delanteras",
                        mecanico.getIdUsuario()
                ));

        ordenService.cambiarEstado(
                orden.getIdOrden(),
                new CambioEstadoRequest(EstadoOrden.CONTROL_FINAL, mecanico.getIdUsuario(), null));
        ordenService.cambiarEstado(
                orden.getIdOrden(),
                new CambioEstadoRequest(EstadoOrden.FINALIZADO, mecanico.getIdUsuario(), null));
        OrdenTrabajo entregada = ordenService.cambiarEstado(
                orden.getIdOrden(),
                new CambioEstadoRequest(EstadoOrden.ENTREGADO, recepcion.getIdUsuario(), "Vehículo entregado"));

        assertNotNull(detalle.getIdDetalle());
        assertEquals(EstadoOrden.ENTREGADO, entregada.getEstado());
        assertNotNull(entregada.getFechaEntrega());

        List<HistorialEstadoOrden> historial = ordenService.obtenerHistorial(orden.getIdOrden());
        assertEquals(5, historial.size());
        assertEquals(EstadoOrden.RECIBIDO, historial.get(0).getEstadoAnterior());
        assertEquals(EstadoOrden.EN_DIAGNOSTICO, historial.get(0).getEstadoNuevo());
        assertEquals(EstadoOrden.FINALIZADO, historial.get(4).getEstadoAnterior());
        assertEquals(EstadoOrden.ENTREGADO, historial.get(4).getEstadoNuevo());
        assertEquals(recepcion.getIdUsuario(), historial.get(4).getUsuario().getIdUsuario());
    }

    @Test
    void permiteCircuitoDeEsperaDeRepuesto() {
        Usuario recepcion = crearUsuario("Recepción", RolUsuario.RECEPCION_ADMINISTRACION);
        Usuario mecanico = crearUsuario("Mecánico", RolUsuario.MECANICO);
        Vehiculo vehiculo = crearVehiculo();
        OrdenTrabajo orden = crearOrden(recepcion, mecanico, vehiculo);

        ordenService.cambiarEstado(
                orden.getIdOrden(),
                new CambioEstadoRequest(EstadoOrden.EN_DIAGNOSTICO, mecanico.getIdUsuario(), null));
        ordenService.cambiarEstado(
                orden.getIdOrden(),
                new CambioEstadoRequest(EstadoOrden.ESPERANDO_REPUESTO, mecanico.getIdUsuario(), "Esperando repuesto"));
        OrdenTrabajo reparacion = ordenService.cambiarEstado(
                orden.getIdOrden(),
                new CambioEstadoRequest(EstadoOrden.EN_REPARACION, mecanico.getIdUsuario(), "Repuesto recibido"));

        assertEquals(EstadoOrden.EN_REPARACION, reparacion.getEstado());
        assertEquals(3, ordenService.obtenerHistorial(orden.getIdOrden()).size());
    }

    @Test
    void rechazaTransicionNoPermitida() {
        Usuario recepcion = crearUsuario("Recepción", RolUsuario.RECEPCION_ADMINISTRACION);
        Usuario mecanico = crearUsuario("Mecánico", RolUsuario.MECANICO);
        Vehiculo vehiculo = crearVehiculo();
        OrdenTrabajo orden = crearOrden(recepcion, mecanico, vehiculo);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ordenService.cambiarEstado(
                        orden.getIdOrden(),
                        new CambioEstadoRequest(EstadoOrden.FINALIZADO, mecanico.getIdUsuario(), null))
        );

        assertTrue(ex.getMessage().contains("Transición inválida"));
    }

    @Test
    void soloMecanicoAsignadoPuedeRegistrarDatosTecnicos() {
        Usuario recepcion = crearUsuario("Recepción", RolUsuario.RECEPCION_ADMINISTRACION);
        Usuario mecanicoAsignado = crearUsuario("Mecánico A", RolUsuario.MECANICO);
        Usuario otroMecanico = crearUsuario("Mecánico B", RolUsuario.MECANICO);
        Vehiculo vehiculo = crearVehiculo();
        OrdenTrabajo orden = crearOrden(recepcion, mecanicoAsignado, vehiculo);

        ordenService.cambiarEstado(
                orden.getIdOrden(),
                new CambioEstadoRequest(EstadoOrden.EN_DIAGNOSTICO, mecanicoAsignado.getIdUsuario(), null));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ordenService.registrarDatosTecnicos(
                        orden.getIdOrden(),
                        new RegistrarDatosTecnicosRequest(
                                "Diagnóstico",
                                null,
                                otroMecanico.getIdUsuario()
                        ))
        );

        assertTrue(ex.getMessage().contains("Sólo el mecánico asignado"));
    }

    @Test
    void detalleSoloPuedeRegistrarloMecanicoAsignadoEnReparacionOControlFinal() {
        Usuario recepcion = crearUsuario("Recepción", RolUsuario.RECEPCION_ADMINISTRACION);
        Usuario mecanico = crearUsuario("Mecánico", RolUsuario.MECANICO);
        Vehiculo vehiculo = crearVehiculo();
        OrdenTrabajo orden = crearOrden(recepcion, mecanico, vehiculo);

        RuntimeException exRecibido = assertThrows(RuntimeException.class, () ->
                detalleService.agregar(
                        orden.getIdOrden(),
                        new AgregarDetalleTrabajoRequest("Trabajo prematuro", mecanico.getIdUsuario()))
        );
        assertTrue(exRecibido.getMessage().contains("No se pueden registrar trabajos"));

        ordenService.cambiarEstado(
                orden.getIdOrden(),
                new CambioEstadoRequest(EstadoOrden.EN_DIAGNOSTICO, mecanico.getIdUsuario(), null));
        ordenService.cambiarEstado(
                orden.getIdOrden(),
                new CambioEstadoRequest(EstadoOrden.EN_REPARACION, mecanico.getIdUsuario(), null));

        DetalleTrabajo detalle = detalleService.agregar(
                orden.getIdOrden(),
                new AgregarDetalleTrabajoRequest("Trabajo válido", mecanico.getIdUsuario()));

        assertNotNull(detalle.getIdDetalle());
    }

    @Test
    void soloRecepcionPuedeRegistrarEntrega() {
        Usuario recepcion = crearUsuario("Recepción", RolUsuario.RECEPCION_ADMINISTRACION);
        Usuario mecanico = crearUsuario("Mecánico", RolUsuario.MECANICO);
        Vehiculo vehiculo = crearVehiculo();
        OrdenTrabajo orden = crearOrden(recepcion, mecanico, vehiculo);

        ordenService.cambiarEstado(
                orden.getIdOrden(),
                new CambioEstadoRequest(EstadoOrden.EN_DIAGNOSTICO, mecanico.getIdUsuario(), null));
        ordenService.cambiarEstado(
                orden.getIdOrden(),
                new CambioEstadoRequest(EstadoOrden.EN_REPARACION, mecanico.getIdUsuario(), null));
        ordenService.cambiarEstado(
                orden.getIdOrden(),
                new CambioEstadoRequest(EstadoOrden.CONTROL_FINAL, mecanico.getIdUsuario(), null));
        ordenService.cambiarEstado(
                orden.getIdOrden(),
                new CambioEstadoRequest(EstadoOrden.FINALIZADO, mecanico.getIdUsuario(), null));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ordenService.cambiarEstado(
                        orden.getIdOrden(),
                        new CambioEstadoRequest(EstadoOrden.ENTREGADO, mecanico.getIdUsuario(), null))
        );
        assertTrue(ex.getMessage().contains("Recepción/Administración"));
    }

    @Test
    void historialDelVehiculoSeDerivaDeSusOrdenes() {
        Usuario recepcion = crearUsuario("Recepción", RolUsuario.RECEPCION_ADMINISTRACION);
        Vehiculo vehiculo = crearVehiculo();

        ordenService.crear(new CrearOrdenRequest(
                vehiculo.getIdVehiculo(), null, 1000, "Primer ingreso", null, recepcion.getIdUsuario()));
        ordenService.crear(new CrearOrdenRequest(
                vehiculo.getIdVehiculo(), null, 2000, "Segundo ingreso", null, recepcion.getIdUsuario()));

        List<OrdenTrabajo> historialVehiculo = ordenService.listarPorVehiculo(vehiculo.getIdVehiculo());

        assertEquals(2, historialVehiculo.size());
        assertTrue(historialVehiculo.stream()
                .allMatch(o -> o.getVehiculo().getIdVehiculo().equals(vehiculo.getIdVehiculo())));
    }

    @Test
    void ingresoSoloPuedeEditarRecepcionMientrasOrdenEstaRecibida() {
        Usuario recepcion = crearUsuario("Recepción", RolUsuario.RECEPCION_ADMINISTRACION);
        Usuario mecanico = crearUsuario("Mecánico", RolUsuario.MECANICO);
        Vehiculo vehiculo = crearVehiculo();
        OrdenTrabajo orden = crearOrden(recepcion, mecanico, vehiculo);

        OrdenTrabajo actualizada = ordenService.actualizarIngreso(
                orden.getIdOrden(),
                new ActualizarIngresoOrdenRequest(
                        1500, "Motivo actualizado", "Observación actualizada", recepcion.getIdUsuario()));

        assertEquals(1500, actualizada.getKilometrajeIngreso());
        assertEquals("Motivo actualizado", actualizada.getMotivoIngreso());

        ordenService.cambiarEstado(
                orden.getIdOrden(),
                new CambioEstadoRequest(EstadoOrden.EN_DIAGNOSTICO, mecanico.getIdUsuario(), null));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ordenService.actualizarIngreso(
                        orden.getIdOrden(),
                        new ActualizarIngresoOrdenRequest(
                                2000, "No debería cambiar", null, recepcion.getIdUsuario()))
        );
        assertTrue(ex.getMessage().contains("No se puede modificar el ingreso"));
    }

    private OrdenTrabajo crearOrden(Usuario recepcion, Usuario mecanico, Vehiculo vehiculo) {
        return ordenService.crear(new CrearOrdenRequest(
                vehiculo.getIdVehiculo(),
                mecanico == null ? null : mecanico.getIdUsuario(),
                1000,
                "Ruido al frenar",
                "Ingreso de prueba",
                recepcion.getIdUsuario()
        ));
    }

    private Usuario crearUsuario(String nombre, RolUsuario rol) {
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(nombre);
        usuario.setRol(rol);
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    private Vehiculo crearVehiculo() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Cliente prueba");
        cliente = clienteRepository.save(cliente);

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPatente(String.format("T%014d", Math.abs(System.nanoTime()) % 100000000000000L));
        vehiculo.setMarca("Toyota");
        vehiculo.setModelo("Corolla");
        vehiculo.setAnio(2020);
        vehiculo.setCliente(cliente);
        return vehiculoRepository.save(vehiculo);
    }
}
