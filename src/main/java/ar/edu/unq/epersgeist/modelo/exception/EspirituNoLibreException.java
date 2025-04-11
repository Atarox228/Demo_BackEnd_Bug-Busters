package ar.edu.unq.epersgeist.modelo.exception;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;

public class EspirituNoLibreException extends RuntimeException {

    private final Espiritu espiritu;

    public EspirituNoLibreException(Espiritu espiritu) {
        this.espiritu = espiritu;
      }

      @Override
      public String getMessage() {
        return "El espiritu [" + espiritu + "] no esta libre";
      }
}
