package it.unibz.dbproject.exceptions;

public class InstantiationException extends FatalException {
    public InstantiationException(String message) {
        super(message);
    }

    public InstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
