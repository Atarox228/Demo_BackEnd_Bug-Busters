package ar.edu.unq.epersgeist.modelo.exception;

public class NoHayAngelesException extends RuntimeException {


    @Override
    public String getMessage() {
        return "No hay angeles para realizar el exorcismo ";
    }
}
