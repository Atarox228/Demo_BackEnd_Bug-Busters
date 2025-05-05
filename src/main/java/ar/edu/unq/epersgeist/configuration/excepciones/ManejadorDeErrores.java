package ar.edu.unq.epersgeist.configuration.excepciones;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import ar.edu.unq.epersgeist.modelo.exception.*;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.servicios.exception.NoHaySantuariosConDemoniosException;
import ar.edu.unq.epersgeist.servicios.exception.PaginaInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ManejadorDeErrores {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarErroresDeValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaginaInvalidaException.class)
    public ResponseEntity<ErrorDetalle> manejarPaginaInvalida(PaginaInvalidaException ex, WebRequest request) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IdNoValidoException.class)
    public ResponseEntity<ErrorDetalle> manejarIdNoValido(IdNoValidoException ex, WebRequest request) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorDetalle> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex, WebRequest request) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoHaySantuariosConDemoniosException.class)
    public ResponseEntity<ErrorDetalle> manejarSantuarioDemoniacoNoEncontrado(NoHaySantuariosConDemoniosException ex, WebRequest request) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExorcismoEnDiferenteUbicacionException.class)
    public ResponseEntity<ErrorDetalle> manejarExorcismoEnUbicacionDiferente(ExorcismoEnDiferenteUbicacionException ex, WebRequest request) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EspirituNoLibreException.class)
    public ResponseEntity<ErrorDetalle> manejarEspirituNoLibre(EspirituNoLibreException ex, WebRequest request) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MovimientoInvalidoException.class)
    public ResponseEntity<ErrorDetalle> manejarMovimientoInvalido(MovimientoInvalidoException ex, WebRequest request) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoHayAngelesException.class)
    public ResponseEntity<ErrorDetalle> manejarSinAngeles(NoHayAngelesException ex, WebRequest request) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InvocacionFallidaPorUbicacionException.class)
    public ResponseEntity<ErrorDetalle> manejarInvocacionNulaPorUbicacion(InvocacionFallidaPorUbicacionException ex, WebRequest request) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NoSePuedenConectarException.class)
    public ResponseEntity<ErrorDetalle> manejarConexionFallida(NoSePuedenConectarException ex, WebRequest request) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetalle> manejarErroresGenerales(Exception ex, WebRequest request) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), "Error interno del servidor");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
