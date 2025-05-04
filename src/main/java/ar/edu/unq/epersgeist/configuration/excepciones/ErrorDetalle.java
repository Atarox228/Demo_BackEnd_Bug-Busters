package ar.edu.unq.epersgeist.configuration.excepciones;

import java.time.LocalDateTime;
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
