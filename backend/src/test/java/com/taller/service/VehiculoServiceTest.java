package com.taller.service;

import com.taller.dto.VehiculoRequest;
import com.taller.entity.Cliente;
import com.taller.entity.Vehiculo;
import com.taller.repository.ClienteRepository;
import com.taller.repository.VehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VehiculoServiceTest {

    @Autowired
    private VehiculoService vehiculoService;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void limpiar() {
        vehiculoRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @Test
    void creaVehiculoNormalizandoPatenteYAsociandoCliente() {
        Cliente cliente = crearCliente("Juan Pérez");

        Vehiculo creado = vehiculoService.crear(new VehiculoRequest(
                " ab 123 cd ", " Toyota ", " Corolla ", 2020, cliente.getIdCliente()
        ));

        assertNotNull(creado.getIdVehiculo());
        assertEquals("AB123CD", creado.getPatente());
        assertEquals("Toyota", creado.getMarca());
        assertEquals("Corolla", creado.getModelo());
        assertEquals(2020, creado.getAnio());
        assertEquals(cliente.getIdCliente(), creado.getCliente().getIdCliente());
    }

    @Test
    void rechazaPatenteDuplicadaSinDistinguirMayusculas() {
        Cliente cliente = crearCliente("Primer Cliente");
        vehiculoService.crear(new VehiculoRequest(
                "AA123BB", "Fiat", "Cronos", 2022, cliente.getIdCliente()
        ));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                vehiculoService.crear(new VehiculoRequest(
                        "aa123bb", "Ford", "Ka", 2018, cliente.getIdCliente()
                ))
        );

        assertTrue(ex.getMessage().contains("Ya existe"));
    }

    @Test
    void actualizaVehiculoYPermiteCambiarSuCliente() {
        Cliente clienteInicial = crearCliente("Cliente Inicial");
        Cliente clienteNuevo = crearCliente("Cliente Nuevo");
        Vehiculo creado = vehiculoService.crear(new VehiculoRequest(
                "AA123BB", "Fiat", "Cronos", 2022, clienteInicial.getIdCliente()
        ));

        Vehiculo actualizado = vehiculoService.actualizar(
                creado.getIdVehiculo(),
                new VehiculoRequest("AB456CD", "Ford", "Focus", 2019, clienteNuevo.getIdCliente())
        );

        assertEquals(creado.getIdVehiculo(), actualizado.getIdVehiculo());
        assertEquals("AB456CD", actualizado.getPatente());
        assertEquals("Ford", actualizado.getMarca());
        assertEquals("Focus", actualizado.getModelo());
        assertEquals(clienteNuevo.getIdCliente(), actualizado.getCliente().getIdCliente());
    }

    @Test
    void buscaPorPatenteMarcaModeloONombreDelCliente() {
        Cliente cliente = crearCliente("María González");
        vehiculoService.crear(new VehiculoRequest(
                "AA123BB", "Toyota", "Corolla", 2020, cliente.getIdCliente()
        ));
        vehiculoService.crear(new VehiculoRequest(
                "AC456DE", "Ford", "Focus", 2018, cliente.getIdCliente()
        ));

        assertEquals(1, vehiculoService.listar("AA123", null).size());
        assertEquals(1, vehiculoService.listar("toyota", null).size());
        assertEquals(1, vehiculoService.listar("corol", null).size());
        assertEquals(2, vehiculoService.listar("González", null).size());
    }

    @Test
    void filtraVehiculosPorCliente() {
        Cliente primero = crearCliente("Primer Cliente");
        Cliente segundo = crearCliente("Segundo Cliente");
        vehiculoService.crear(new VehiculoRequest(
                "AA123BB", "Fiat", "Cronos", 2022, primero.getIdCliente()
        ));
        vehiculoService.crear(new VehiculoRequest(
                "AC456DE", "Ford", "Focus", 2018, segundo.getIdCliente()
        ));

        List<Vehiculo> resultado = vehiculoService.listar(null, primero.getIdCliente());

        assertEquals(1, resultado.size());
        assertEquals("AA123BB", resultado.get(0).getPatente());
    }

    @Test
    void rechazaAltaConClienteInexistente() {
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                vehiculoService.crear(new VehiculoRequest(
                        "AA123BB", "Fiat", "Cronos", 2022, 999999L
                ))
        );

        assertTrue(ex.getMessage().contains("Cliente no encontrado"));
    }

    @Test
    void informaVehiculoInexistente() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> vehiculoService.obtener(999999L));

        assertTrue(ex.getMessage().contains("Vehículo no encontrado"));
    }

    @Test
    void rechazaActualizarConPatenteDeOtroVehiculo() {
        Cliente cliente = crearCliente("Cliente Prueba");
        Vehiculo primero = vehiculoService.crear(new VehiculoRequest(
                "AA123BB", "Fiat", "Cronos", 2022, cliente.getIdCliente()
        ));
        vehiculoService.crear(new VehiculoRequest(
                "AC456DE", "Ford", "Focus", 2018, cliente.getIdCliente()
        ));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                vehiculoService.actualizar(
                        primero.getIdVehiculo(),
                        new VehiculoRequest("AC456DE", "Fiat", "Cronos", 2022, cliente.getIdCliente())
                )
        );

        assertTrue(ex.getMessage().contains("Ya existe"));
    }

    private Cliente crearCliente(String nombre) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        return clienteRepository.save(cliente);
    }
}
