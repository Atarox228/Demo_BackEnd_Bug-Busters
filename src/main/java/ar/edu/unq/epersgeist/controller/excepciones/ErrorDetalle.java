package ar.edu.unq.epersgeist.controller.excepciones;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.*;

@Getter @Setter
public class ErrorDetalle {
    private LocalDateTime timestamp;
    private String mensaje;


    public ErrorDetalle(LocalDateTime timestamp, String mensaje) {
        this.timestamp = timestamp;
        this.mensaje = mensaje;
    }


}
