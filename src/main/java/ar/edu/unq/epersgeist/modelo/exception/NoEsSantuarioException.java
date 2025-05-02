package ar.edu.unq.epersgeist.modelo.exception;

public class NoEsSantuarioException extends RuntimeException {

    @Override
    public String getMessage() {
        return "La ubicacion a ingresar no es santuario";
    }
}
