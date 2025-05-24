package ar.edu.unq.epersgeist.controller.excepciones;

import ar.edu.unq.epersgeist.controller.EstadisicaticaControllerREST;
import ar.edu.unq.epersgeist.servicios.exception.NoHaySantuariosConDemoniosException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Order(3)
@RestControllerAdvice(assignableTypes = {EstadisicaticaControllerREST.class})
public class ReportesExcepciones {

    @ExceptionHandler(NoHaySantuariosConDemoniosException.class)
    public ResponseEntity<ErrorDetalle> manejarSantuarioDemoniacoNoEncontrado(NoHaySantuariosConDemoniosException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
