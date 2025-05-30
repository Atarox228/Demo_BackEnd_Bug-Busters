package ar.edu.unq.epersgeist.controller.excepciones;

public class ArgumentoNoValidoException extends RuntimeException {
    public ArgumentoNoValidoException(String message) {
        super(message);
    }
}
