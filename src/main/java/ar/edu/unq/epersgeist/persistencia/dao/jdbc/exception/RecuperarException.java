package ar.edu.unq.epersgeist.persistencia.dao.jdbc.exception;

public class RecuperarException extends RuntimeException {
    public RecuperarException() {
        super("No se encontro el espiritu");
    }
}
