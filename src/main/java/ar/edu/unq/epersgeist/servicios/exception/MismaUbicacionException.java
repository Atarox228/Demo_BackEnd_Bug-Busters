package ar.edu.unq.epersgeist.servicios.exception;

public class MismaUbicacionException extends RuntimeException {
    public MismaUbicacionException() {
        super("No se puede conectar a una misma ubicacion");
    }
}
