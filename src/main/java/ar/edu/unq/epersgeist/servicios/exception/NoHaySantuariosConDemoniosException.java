package ar.edu.unq.epersgeist.servicios.exception;

public class NoHaySantuariosConDemoniosException extends RuntimeException {

    public NoHaySantuariosConDemoniosException(String mensaje) {
        super(mensaje);
    }

    public NoHaySantuariosConDemoniosException() {
        super("No se encuentra ningun santuario con demonios");
    }

}
