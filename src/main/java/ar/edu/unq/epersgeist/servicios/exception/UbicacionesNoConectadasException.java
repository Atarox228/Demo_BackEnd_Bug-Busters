package ar.edu.unq.epersgeist.servicios.exception;

public class UbicacionesNoConectadasException extends RuntimeException {
    public UbicacionesNoConectadasException() {
        super("Las ubicaciones no estan conectadas por ningun camino");
    }
}
