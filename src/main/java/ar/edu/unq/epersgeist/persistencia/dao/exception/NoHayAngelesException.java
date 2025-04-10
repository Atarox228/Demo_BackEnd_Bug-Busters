package ar.edu.unq.epersgeist.persistencia.dao.exception;

public class NoHayAngelesException extends RuntimeException {


    @Override
    public String getMessage() {
        return "No hay angeles para realizar el exorcismo ";
    }
}
