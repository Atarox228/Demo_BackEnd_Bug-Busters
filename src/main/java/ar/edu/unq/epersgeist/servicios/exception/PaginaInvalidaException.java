package ar.edu.unq.epersgeist.servicios.exception;

public class PaginaInvalidaException extends RuntimeException {

    public PaginaInvalidaException(String message) {
        super(message);
    }

    public PaginaInvalidaException() {
        super("La página o cantidad de páginas no son válidas");
    }

}


