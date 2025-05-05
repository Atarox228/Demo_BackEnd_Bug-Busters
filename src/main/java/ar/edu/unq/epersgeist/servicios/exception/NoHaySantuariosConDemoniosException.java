package ar.edu.unq.epersgeist.servicios.exception;

public class NoHaySantuariosConDemoniosException extends RuntimeException {

    public NoHaySantuariosConDemoniosException() {
        super("No se encuentra ningun santuario con demonios");
    }

}
