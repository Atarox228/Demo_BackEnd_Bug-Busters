package ar.edu.unq.epersgeist.controller.excepciones;

public class EntidadSinUbicacionException extends RuntimeException {

    public <T> EntidadSinUbicacionException(T entidad, Long id) {
        super(entidad.getClass().getName() +"con ID:"  + id + " deberia estar en una ubicacion");
    }
}
