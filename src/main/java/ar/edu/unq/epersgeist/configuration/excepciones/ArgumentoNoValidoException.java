package ar.edu.unq.epersgeist.configuration.excepciones;

public class ArgumentoNoValidoException extends RuntimeException {
    public ArgumentoNoValidoException(String message) {
        super(message);
    }
}
