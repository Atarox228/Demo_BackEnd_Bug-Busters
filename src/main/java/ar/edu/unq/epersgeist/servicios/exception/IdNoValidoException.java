package ar.edu.unq.epersgeist.servicios.exception;

public class IdNoValidoException extends RuntimeException {

    public IdNoValidoException() {
        super("El id no es valido");
    }

    public IdNoValidoException(Long id) {

        super("El id: " + id + " no es valido");
    }

    public IdNoValidoException(String mensaje) {
        super(mensaje);
    }
}
