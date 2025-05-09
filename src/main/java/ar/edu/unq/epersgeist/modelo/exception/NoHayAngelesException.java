package ar.edu.unq.epersgeist.modelo.exception;

public class NoHayAngelesException extends RuntimeException {

    public NoHayAngelesException() {
        super("No hay angeles para realizar el exorcismo");
    }

}
