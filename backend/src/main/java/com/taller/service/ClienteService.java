package com.taller.service;

import com.taller.dto.ClienteRequest;
import com.taller.entity.Cliente;
import com.taller.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar(String buscar) {
        if (buscar == null || buscar.isBlank()) {
            return clienteRepository.findAllByOrderByNombreAsc();
        }

        String criterio = buscar.trim();
        return clienteRepository
                .findByNombreContainingIgnoreCaseOrDniCuitContainingIgnoreCaseOrderByNombreAsc(
                        criterio,
                        criterio
                );
    }

    @Transactional(readOnly = true)
    public Cliente obtener(Long idCliente) {
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + idCliente));
    }

    @Transactional
    public Cliente crear(ClienteRequest request) {
        String dniCuit = normalizarOpcional(request.dniCuit());
        validarDniCuitDisponible(dniCuit, null);

        Cliente cliente = new Cliente();
        aplicarDatos(cliente, request, dniCuit);

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente actualizar(Long idCliente, ClienteRequest request) {
        Cliente cliente = obtener(idCliente);
        String dniCuit = normalizarOpcional(request.dniCuit());
        validarDniCuitDisponible(dniCuit, idCliente);

        aplicarDatos(cliente, request, dniCuit);
        return clienteRepository.save(cliente);
    }

    private void aplicarDatos(Cliente cliente, ClienteRequest request, String dniCuit) {
        cliente.setNombre(normalizarNombre(request.nombre()));
        cliente.setDniCuit(dniCuit);
        cliente.setTelefono(normalizarOpcional(request.telefono()));
        cliente.setEmail(normalizarEmail(request.email()));
        cliente.setDireccion(normalizarOpcional(request.direccion()));
    }

    private void validarDniCuitDisponible(String dniCuit, Long idClienteActual) {
        if (dniCuit == null) {
            return;
        }

        boolean ocupado = idClienteActual == null
                ? clienteRepository.existsByDniCuitIgnoreCase(dniCuit)
                : clienteRepository.existsByDniCuitIgnoreCaseAndIdClienteNot(dniCuit, idClienteActual);

        if (ocupado) {
            throw new RuntimeException("Ya existe un cliente con el DNI/CUIT: " + dniCuit);
        }
    }

    private String normalizarNombre(String nombre) {
        return nombre.trim().replaceAll("\\s+", " ");
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String limpio = valor.trim();
        return limpio.isEmpty() ? null : limpio;
    }

    private String normalizarEmail(String email) {
        String limpio = normalizarOpcional(email);
        return limpio == null ? null : limpio.toLowerCase(Locale.ROOT);
    }
}
