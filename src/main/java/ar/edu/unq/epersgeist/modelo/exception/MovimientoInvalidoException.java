package ar.edu.unq.epersgeist.modelo.exception;

public class MovimientoInvalidoException extends RuntimeException{

    @Override
    public String getMessage() {
        return "El medium ya se encuentra en la ubicación";
    }
}
