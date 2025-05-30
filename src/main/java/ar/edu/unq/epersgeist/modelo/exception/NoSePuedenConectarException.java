package ar.edu.unq.epersgeist.modelo.exception;


import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;

public class NoSePuedenConectarException extends RuntimeException {

    private final Medium medium;
    private final Espiritu espiritu;


    public NoSePuedenConectarException(Medium medium, Espiritu espiritu) {
        this.medium = medium;
        this.espiritu = espiritu;
    }

    @Override
    public String getMessage() {
        return "El medium con ID:" + medium.getId() + " y el espiritu con ID:" + espiritu.getId() + " no pueden conectarse";
    }

}
