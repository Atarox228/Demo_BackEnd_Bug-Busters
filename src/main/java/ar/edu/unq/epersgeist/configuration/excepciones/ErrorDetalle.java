package ar.edu.unq.epersgeist.configuration.excepciones;

import java.time.LocalDateTime;
import lombok.*;

@Getter @Setter
public class ErrorDetalle {
    private LocalDateTime timestamp;
    private String mensaje;
    private String detalles;

    public ErrorDetalle(LocalDateTime timestamp, String mensaje, String detalles) {
        this.timestamp = timestamp;
        this.mensaje = mensaje;
        this.detalles = detalles;
    }
}
