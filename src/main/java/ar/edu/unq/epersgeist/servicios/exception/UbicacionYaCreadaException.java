package ar.edu.unq.epersgeist.servicios.exception;

public class UbicacionYaCreadaException extends RuntimeException {
    public UbicacionYaCreadaException(String nombre) {
        super("La ubicacion "+ nombre + " ya existe");
    }
}
