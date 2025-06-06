package ar.edu.unq.epersgeist.servicios.exception;

public class FueraDeRangoDistanciaException extends RuntimeException {
    public FueraDeRangoDistanciaException() {
        super("Dominio fallido, la distancia entre espiritus nos es la requerida");
    }
}
