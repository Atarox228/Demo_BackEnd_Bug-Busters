package ar.edu.unq.epersgeist.persistencia.dao.exception;

public class EntidadYaRegistradaException extends RuntimeException {


    public <T> EntidadYaRegistradaException(T entity) {

        super("Entidad ya registrada "+ entity.getClass().getName());
    }
}
