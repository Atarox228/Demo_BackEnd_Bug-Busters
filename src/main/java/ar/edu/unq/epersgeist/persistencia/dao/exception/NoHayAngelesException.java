package ar.edu.unq.epersgeist.persistencia.dao.exception;

public class NoHayAngelesException extends RuntimeException {


    @Override
    public String getMessage() {
        return "No Hay Angeles Para Realizar El Exorcismo ";
    }
}
