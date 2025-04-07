package ar.edu.unq.epersgeist.modelo.exception;

import ar.edu.unq.epersgeist.modelo.Ubicacion;

public class EntidadConUbicacionRegistradaException extends RuntimeException {
    public <T> EntidadConUbicacionRegistradaException(T entity, Ubicacion ubicacion) {
        super("Entidad: " + entity.getClass().getName() + " ya registrada en:" + ubicacion.getNombre());
    }
}
