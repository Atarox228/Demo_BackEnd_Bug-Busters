package ar.edu.unq.epersgeist.servicios.exception;

public class PaginaInvalidaException extends RuntimeException {

    public PaginaInvalidaException(String mensaje) {
        super(mensaje);
    }

    public PaginaInvalidaException() {
        super("La pagina ingresada no es valida");
    }

}


