package ar.edu.unq.epersgeist.modelo.exception;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion;

public class InvocacionFallidaPorUbicacionException extends RuntimeException {
    private final Espiritu espiritu;
    private final Ubicacion ubicacion;

    public InvocacionFallidaPorUbicacionException(Espiritu espiritu, Ubicacion ubicacion) {
        this.espiritu = espiritu;
        this.ubicacion = ubicacion;
    }

    @Override
    public String getMessage() {
        return "No se puede invocar al espiritu: [ " + espiritu + " ] en esta ubicacion: [ " + ubicacion + " ]";
    }

}
