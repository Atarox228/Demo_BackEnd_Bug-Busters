package ar.edu.unq.epersgeist.servicios.exception;

public class sinResultadosException extends RuntimeException {
    public sinResultadosException() {
        super("No se tuvieron resultados para la solicitud. Causas: algun Id invalido o Base de datos vacia");
    }
}
