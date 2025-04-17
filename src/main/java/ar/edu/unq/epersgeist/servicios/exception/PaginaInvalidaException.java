package ar.edu.unq.epersgeist.servicios.exception;

public class PaginaInvalidaException extends RuntimeException {

        @Override
        public String getMessage() {
            return "La pagina ingresada no es valida";
        }
}


