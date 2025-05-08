package ar.edu.unq.epersgeist.servicios.exception;

public class EntidadEliminadaException extends RuntimeException {

    public <T> EntidadEliminadaException(T entidad) {
        super("La entidad: "+entidad.getClass().getName() +" se encuentra eliminada");
    }
}
