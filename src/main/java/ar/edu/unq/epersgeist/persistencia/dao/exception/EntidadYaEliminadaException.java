package ar.edu.unq.epersgeist.persistencia.dao.exception;

public class EntidadYaEliminadaException extends RuntimeException {
    public <T> EntidadYaEliminadaException(T entity) {
        super("Entidad ya eliminada: " + entity.getClass().getName());
    }
}
