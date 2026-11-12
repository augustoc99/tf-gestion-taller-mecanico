package com.taller.service;

import com.taller.dto.VehiculoRequest;
import com.taller.entity.Cliente;
import com.taller.entity.Vehiculo;
import com.taller.repository.ClienteRepository;
import com.taller.repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;

    public VehiculoService(
            VehiculoRepository vehiculoRepository,
            ClienteRepository clienteRepository
    ) {
        this.vehiculoRepository = vehiculoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> listar(String buscar, Long idCliente) {
        if (idCliente != null && !clienteRepository.existsById(idCliente)) {
            throw new RuntimeException("Cliente no encontrado: " + idCliente);
        }

        String criterio = normalizarOpcional(buscar);
        return vehiculoRepository.buscar(idCliente, criterio);
    }

    @Transactional(readOnly = true)
    public Vehiculo obtener(Long idVehiculo) {
        return vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado: " + idVehiculo));
    }

    @Transactional
    public Vehiculo crear(VehiculoRequest request) {
        String patente = normalizarPatente(request.patente());
        validarPatenteDisponible(patente, null);

        Cliente cliente = obtenerCliente(request.idCliente());
        Vehiculo vehiculo = new Vehiculo();
        aplicarDatos(vehiculo, request, patente, cliente);
        return vehiculoRepository.save(vehiculo);
    }

    @Transactional
    public Vehiculo actualizar(Long idVehiculo, VehiculoRequest request) {
        Vehiculo vehiculo = obtener(idVehiculo);
        String patente = normalizarPatente(request.patente());
        validarPatenteDisponible(patente, idVehiculo);

        Cliente cliente = obtenerCliente(request.idCliente());
        aplicarDatos(vehiculo, request, patente, cliente);
        return vehiculoRepository.save(vehiculo);
    }

    private Cliente obtenerCliente(Long idCliente) {
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + idCliente));
    }

    private void validarPatenteDisponible(String patente, Long idVehiculoActual) {
        boolean ocupada = idVehiculoActual == null
                ? vehiculoRepository.existsByPatenteIgnoreCase(patente)
                : vehiculoRepository.existsByPatenteIgnoreCaseAndIdVehiculoNot(patente, idVehiculoActual);

        if (ocupada) {
            throw new RuntimeException("Ya existe un vehículo con la patente: " + patente);
        }
    }

    private void aplicarDatos(
            Vehiculo vehiculo,
            VehiculoRequest request,
            String patente,
            Cliente cliente
    ) {
        vehiculo.setPatente(patente);
        vehiculo.setMarca(normalizarTexto(request.marca()));
        vehiculo.setModelo(normalizarTexto(request.modelo()));
        vehiculo.setAnio(request.anio());
        vehiculo.setCliente(cliente);
    }

    private String normalizarPatente(String patente) {
        return patente.trim().replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
    }

    private String normalizarTexto(String valor) {
        return valor.trim().replaceAll("\\s+", " ");
    }

    private String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String limpio = valor.trim();
        return limpio.isEmpty() ? null : limpio;
    }
}
