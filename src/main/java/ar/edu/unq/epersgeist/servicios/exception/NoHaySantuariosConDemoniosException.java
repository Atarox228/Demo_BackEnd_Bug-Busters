package ar.edu.unq.epersgeist.servicios.exception;

public class NoHaySantuariosConDemoniosException extends RuntimeException {

    @Override
    public String getMessage() {
        return "No se encuentra ningun santuario con demonios";
    }
}
