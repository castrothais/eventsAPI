
package br.edu.ifsudestemg.eventsAPI.api.exeption;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(
            ConstraintViolationException ex) {

        Map<String, String> erros = new LinkedHashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            erros.put(
                    violation.getPropertyPath().toString(),
                    violation.getMessage()
            );
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, String>> handleRegraNegocio(
            RegraNegocioException ex) {

        Map<String, String> erro = new LinkedHashMap<>();
        erro.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }
}
