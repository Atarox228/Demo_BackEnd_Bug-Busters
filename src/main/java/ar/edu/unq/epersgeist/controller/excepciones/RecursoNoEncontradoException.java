package ar.edu.unq.epersgeist.controller.excepciones;

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String message) {
        super(message);
    }
    public RecursoNoEncontradoException(){
        super("Entidad no encontrada");
    }
}
