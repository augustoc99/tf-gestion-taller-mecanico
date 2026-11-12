package com.taller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record VehiculoRequest(
        @NotBlank(message = "La patente es obligatoria")
        @Size(max = 15, message = "La patente no puede superar 15 caracteres")
        String patente,

        @NotBlank(message = "La marca es obligatoria")
        @Size(max = 60, message = "La marca no puede superar 60 caracteres")
        String marca,

        @NotBlank(message = "El modelo es obligatorio")
        @Size(max = 60, message = "El modelo no puede superar 60 caracteres")
        String modelo,

        @Min(value = 1886, message = "El año no puede ser anterior a 1886")
        @Max(value = 2100, message = "El año no puede superar 2100")
        Integer anio,

        @NotNull(message = "Debe indicar el cliente al que pertenece el vehículo")
        @Positive(message = "El identificador del cliente debe ser positivo")
        Long idCliente
) {
}
