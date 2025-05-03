package ar.edu.unq.epersgeist.modelo.exception;

public class ExorcismoEnDiferenteUbicacionException extends RuntimeException {

    public ExorcismoEnDiferenteUbicacionException() {
        super();
    }

    public String getMessage() {
        return "no se puede exorcizar en diferentes ubicaciones";
    }
}
