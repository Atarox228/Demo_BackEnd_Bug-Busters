package ar.edu.unq.epersgeist.servicios.exception;

import ar.edu.unq.epersgeist.modelo.Ubicacion;

public class UbicacionEnlazadaConEntidadesException extends RuntimeException {
    public UbicacionEnlazadaConEntidadesException(Ubicacion ubicacion) {
        super("La ubicacion: " + ubicacion.getNombre() + " tiene entidades registradas, se deben remover antes de poder eliminarse");
    }
}
