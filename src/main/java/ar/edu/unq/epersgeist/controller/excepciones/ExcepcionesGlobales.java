package ar.edu.unq.epersgeist.controller.excepciones;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import ar.edu.unq.epersgeist.modelo.exception.*;
import ar.edu.unq.epersgeist.servicios.exception.*;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class ExcepcionesGlobales {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarErroresDeValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ArgumentoNoValidoException.class)
    public ResponseEntity<ErrorDetalle>  manejarErroresDeValidacion(ArgumentoNoValidoException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaginaInvalidaException.class)
    public ResponseEntity<ErrorDetalle> manejarPaginaInvalida(PaginaInvalidaException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IdNoValidoException.class)
    public ResponseEntity<ErrorDetalle> manejarIdNoValido(IdNoValidoException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorDetalle> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntidadConEntidadesConectadasException.class)
    public ResponseEntity<ErrorDetalle> manejarEliminarEntidadConEntidades(EntidadConEntidadesConectadasException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntidadSinUbicacionException.class)
    public ResponseEntity<ErrorDetalle> manejarEntidadSinUbicacion(EntidadSinUbicacionException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(EntidadEliminadaException.class)
    public ResponseEntity<ErrorDetalle> manejarEntidadEliminada(EntidadEliminadaException ex) {
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.GONE);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDetalle> manejarRutaNoEncontrada(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDetalle(LocalDateTime.now(),"Ruta no válida: " + ex.getRequestURL()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDetalle> handleJsonParseError(HttpMessageNotReadableException ex) {
        String message = extractEnumErrorMessage(ex);
        return ResponseEntity
                .badRequest()
                .body(new ErrorDetalle(LocalDateTime.now(),message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDetalle> handleEnumMismatch(MethodArgumentTypeMismatchException ex) {
        String nombre = ex.getName();
        Class<?> tipoEsperado = ex.getRequiredType();
        if (ex.getRequiredType().isEnum()) {
            String valoresPermitidos = Arrays.stream(ex.getRequiredType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorDetalle(LocalDateTime.now(),"Valor inválido para '" + ex.getName() + "'. Valores permitidos: " + valoresPermitidos + "."));
        } else if (tipoEsperado != null && tipoEsperado.equals(Long.class)) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorDetalle(LocalDateTime.now(),"El parámetro '" + nombre + "' debe ser un número válido (long)."));
        } else if (tipoEsperado != null && tipoEsperado.equals(Integer.class)) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorDetalle(LocalDateTime.now(),"El parámetro '" + nombre + "' debe ser un número válido (Integer)."));
        }
        return ResponseEntity
                .badRequest()
                .body(new ErrorDetalle(LocalDateTime.now(),"Error de parámetro: " + ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetalle> manejarErroresGenerales(Exception ex) {
        ex.printStackTrace(); //exclusivo para debugeo
        ErrorDetalle error = new ErrorDetalle(LocalDateTime.now(), "Error interno del servidor");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String extractEnumErrorMessage(HttpMessageNotReadableException ex) {
        Throwable root = ex.getMostSpecificCause();

        if (root instanceof InvalidFormatException invalidFormat) {

            String campo = invalidFormat.getPath().getFirst().getFieldName();
            Class<?> tipo = invalidFormat.getTargetType();

            if (tipo.isEnum()) {
                String valoresPermitidos = Arrays.stream(tipo.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                return "Valor inválido para '" + campo + "'. Valores permitidos: " + valoresPermitidos + ".";
            }
        }

        return "Error al interpretar el cuerpo de la solicitud.";
    }


}
