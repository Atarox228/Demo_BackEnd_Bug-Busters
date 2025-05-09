package ar.edu.unq.epersgeist.servicios.exception;

public class MovimientoInvalidoException extends RuntimeException {

    public MovimientoInvalidoException() {
        super("El medium ya se encuentra en la ubicación");
    }
}
