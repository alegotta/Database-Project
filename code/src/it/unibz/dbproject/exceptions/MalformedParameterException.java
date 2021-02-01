package it.unibz.dbproject.exceptions;

public class MalformedParameterException extends NonFatalDbException {
    public MalformedParameterException(String sqlCode, String message, String operation, String details, Throwable cause) {
        super(sqlCode, message, operation, details, cause);
    }

    public MalformedParameterException(String sqlCode, String message, String operation, String details) {
        super(sqlCode, message, operation, details);
    }

    public MalformedParameterException(String sqlCode, String operation, String details) {
        super(sqlCode, "Malformed input parameter", operation, details);
    }

    public MalformedParameterException(String operation, String details) {
        super("22030", "Malformed input parameter", operation, details);
    }

    public MalformedParameterException(String sqlCode, String operation, String details, Throwable cause) {
        super(sqlCode, "Malformed input parameter", operation, details, cause);
    }
}
