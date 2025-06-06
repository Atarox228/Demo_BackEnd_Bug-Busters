package ar.edu.unq.epersgeist.modelo.exception;

import ar.edu.unq.epersgeist.modelo.Espiritu;

public class NoSePuedeDominarException extends RuntimeException {
    public NoSePuedeDominarException(Espiritu espiritu) {
        super("El espiritu  " + espiritu.getNombre() + " es mi dominante");
    }
}
