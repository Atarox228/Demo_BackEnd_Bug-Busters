package ar.edu.unq.epersgeist.servicios.exception;

public class EntidadConEntidadesConectadasException extends RuntimeException {
    public <T> EntidadConEntidadesConectadasException(T entidad) {
        super("La entidad:"+ entidad.getClass().getName()+" No puede ser eliminada porque tiene entidades conectadas");
    }
}
