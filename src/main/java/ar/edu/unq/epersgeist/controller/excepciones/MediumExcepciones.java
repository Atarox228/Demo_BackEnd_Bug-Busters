package ar.edu.unq.epersgeist.controller.excepciones;

import ar.edu.unq.epersgeist.modelo.exception.*;
import ar.edu.unq.epersgeist.servicios.exception.MovimientoInvalidoException;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Order(2)
@RestControllerAdvice
public class MediumExcepciones {

    @ExceptionHandler(MovimientoInvalidoException.class)
    public ResponseEntity<ErrorDetalle> manejarMovimientoInvalido(MovimientoInvalidoException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EspirituNoLibreException.class)
    public ResponseEntity<ErrorDetalle> manejarEspirituNoLibre(EspirituNoLibreException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ExorcismoEnDiferenteUbicacionException.class)
    public ResponseEntity<ErrorDetalle> manejarExorcismoEnUbicacionDiferente(ExorcismoEnDiferenteUbicacionException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MismoMediumException.class)
    public ResponseEntity<ErrorDetalle> manejarExorcirsoConUnoMismo(MismoMediumException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoSePuedenConectarException.class)
    public ResponseEntity<ErrorDetalle> manejarConexionFallida(NoSePuedenConectarException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InvocacionFallidaPorUbicacionException.class)
    public ResponseEntity<ErrorDetalle> manejarInvocacionNulaPorUbicacion(InvocacionFallidaPorUbicacionException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NoHayAngelesException.class)
    public ResponseEntity<ErrorDetalle> manejarSinAngeles(NoHayAngelesException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
