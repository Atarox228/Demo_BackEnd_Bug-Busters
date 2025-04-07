package ar.edu.unq.epersgeist.servicios.exception;

public class IdNoValidoException extends RuntimeException {
    public IdNoValidoException(Long id) {

        super("El id: " + id + " no es valido");
    }
}
