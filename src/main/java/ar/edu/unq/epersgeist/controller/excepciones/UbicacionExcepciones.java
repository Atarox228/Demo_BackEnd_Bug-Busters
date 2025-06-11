package ar.edu.unq.epersgeist.controller.excepciones;


import ar.edu.unq.epersgeist.servicios.exception.UbicacionYaCreadaException;
import ar.edu.unq.epersgeist.servicios.exception.sinResultadosException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Order(1)
@RestControllerAdvice()
public class UbicacionExcepciones {

    @ExceptionHandler(UbicacionYaCreadaException.class)
    public ResponseEntity<ErrorDetalle> manejarUbicacionYaCreada(UbicacionYaCreadaException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(sinResultadosException.class)
    public ResponseEntity<ErrorDetalle> manejarSinResultados(sinResultadosException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

}
