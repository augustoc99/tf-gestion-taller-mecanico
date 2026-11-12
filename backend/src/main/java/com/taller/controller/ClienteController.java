package com.taller.controller;

import com.taller.dto.ClienteRequest;
import com.taller.dto.ClienteResponse;
import com.taller.entity.Cliente;
import com.taller.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<ClienteResponse> listar(@RequestParam(required = false) String buscar) {
        return clienteService.listar(buscar).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{idCliente}")
    public ClienteResponse obtener(@PathVariable Long idCliente) {
        return toResponse(clienteService.obtener(idCliente));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse crear(@Valid @RequestBody ClienteRequest request) {
        return toResponse(clienteService.crear(request));
    }

    @PutMapping("/{idCliente}")
    public ClienteResponse actualizar(
            @PathVariable Long idCliente,
            @Valid @RequestBody ClienteRequest request) {
        return toResponse(clienteService.actualizar(idCliente, request));
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getIdCliente(),
                cliente.getNombre(),
                cliente.getDniCuit(),
                cliente.getTelefono(),
                cliente.getEmail(),
                cliente.getDireccion(),
                cliente.getFechaRegistro()
        );
    }
}
