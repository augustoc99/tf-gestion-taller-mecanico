package com.taller.service;

import com.taller.dto.ClienteRequest;
import com.taller.entity.Cliente;
import com.taller.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ClienteServiceTest {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void limpiar() {
        clienteRepository.deleteAll();
    }

    @Test
    void creaClienteNormalizandoDatos() {
        Cliente creado = clienteService.crear(new ClienteRequest(
                "  Juan   Pérez  ",
                "  30111222  ",
                " 351 555-1000 ",
                "  JUAN@EJEMPLO.COM ",
                "  Av. Siempre Viva 123  "
        ));

        assertNotNull(creado.getIdCliente());
        assertEquals("Juan Pérez", creado.getNombre());
        assertEquals("30111222", creado.getDniCuit());
        assertEquals("351 555-1000", creado.getTelefono());
        assertEquals("juan@ejemplo.com", creado.getEmail());
        assertEquals("Av. Siempre Viva 123", creado.getDireccion());
        assertNotNull(creado.getFechaRegistro());
    }

    @Test
    void permiteDatosOpcionalesVacios() {
        Cliente creado = clienteService.crear(new ClienteRequest(
                "Cliente Sin Datos Opcionales",
                "   ",
                " ",
                "",
                null
        ));

        assertNull(creado.getDniCuit());
        assertNull(creado.getTelefono());
        assertNull(creado.getEmail());
        assertNull(creado.getDireccion());
    }

    @Test
    void rechazaDniCuitDuplicado() {
        clienteService.crear(new ClienteRequest(
                "Primer Cliente", "20-12345678-9", null, null, null));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                clienteService.crear(new ClienteRequest(
                        "Segundo Cliente", "20-12345678-9", null, null, null))
        );

        assertTrue(ex.getMessage().contains("Ya existe"));
    }

    @Test
    void actualizaClienteManteniendoIdentidad() {
        Cliente creado = clienteService.crear(new ClienteRequest(
                "Nombre Inicial", "12345678", null, null, null));

        Cliente actualizado = clienteService.actualizar(
                creado.getIdCliente(),
                new ClienteRequest(
                        "Nombre Actualizado",
                        "12345678",
                        "3515559999",
                        "nuevo@ejemplo.com",
                        "Nueva dirección"
                )
        );

        assertEquals(creado.getIdCliente(), actualizado.getIdCliente());
        assertEquals("Nombre Actualizado", actualizado.getNombre());
        assertEquals("3515559999", actualizado.getTelefono());
        assertEquals("nuevo@ejemplo.com", actualizado.getEmail());
        assertEquals("Nueva dirección", actualizado.getDireccion());
    }

    @Test
    void buscaPorNombreODniCuit() {
        clienteService.crear(new ClienteRequest(
                "Ana González", "27123456", null, null, null));
        clienteService.crear(new ClienteRequest(
                "Carlos Pérez", "30987654", null, null, null));

        List<Cliente> porNombre = clienteService.listar("gonz");
        List<Cliente> porDocumento = clienteService.listar("3098");

        assertEquals(1, porNombre.size());
        assertEquals("Ana González", porNombre.get(0).getNombre());
        assertEquals(1, porDocumento.size());
        assertEquals("Carlos Pérez", porDocumento.get(0).getNombre());
    }

    @Test
    void informaClienteInexistente() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.obtener(999999L));

        assertTrue(ex.getMessage().contains("Cliente no encontrado"));
    }
}
