package com.taller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        String mensaje = ex.getMessage() == null ? "" : ex.getMessage();

        if (mensaje.contains("no encontrada") || mensaje.contains("no encontrado")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "NO_ENCONTRADO", "detalle", mensaje));
        }
        if (mensaje.contains("Transición inválida")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "TRANSICION_INVALIDA", "detalle", mensaje));
        }
        if (mensaje.contains("No se puede")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "OPERACION_INVALIDA", "detalle", mensaje));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "ERROR_INTERNO", "detalle", mensaje));
    }
}