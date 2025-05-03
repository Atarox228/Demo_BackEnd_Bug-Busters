package ar.edu.unq.epersgeist.modelo.exception;

public class MismoMediumException extends RuntimeException {

    public String getMessage(){
        return "un medium no se puede exorcizar a el mismo";
    }
}
