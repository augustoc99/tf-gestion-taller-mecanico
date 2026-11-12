package com.taller.controller;

import com.taller.dto.VehiculoRequest;
import com.taller.dto.VehiculoResponse;
import com.taller.entity.Vehiculo;
import com.taller.service.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public List<VehiculoResponse> listar(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) Long idCliente
    ) {
        return vehiculoService.listar(buscar, idCliente).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{idVehiculo}")
    public VehiculoResponse obtener(@PathVariable Long idVehiculo) {
        return toResponse(vehiculoService.obtener(idVehiculo));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculoResponse crear(@Valid @RequestBody VehiculoRequest request) {
        return toResponse(vehiculoService.crear(request));
    }

    @PutMapping("/{idVehiculo}")
    public VehiculoResponse actualizar(
            @PathVariable Long idVehiculo,
            @Valid @RequestBody VehiculoRequest request
    ) {
        return toResponse(vehiculoService.actualizar(idVehiculo, request));
    }

    private VehiculoResponse toResponse(Vehiculo vehiculo) {
        return new VehiculoResponse(
                vehiculo.getIdVehiculo(),
                vehiculo.getPatente(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getAnio(),
                vehiculo.getCliente().getIdCliente(),
                vehiculo.getCliente().getNombre(),
                vehiculo.getCliente().getDniCuit()
        );
    }
}
