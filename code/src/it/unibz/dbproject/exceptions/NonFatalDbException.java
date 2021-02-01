package it.unibz.dbproject.exceptions;

public class NonFatalDbException extends DbException {
    public NonFatalDbException(String sqlCode, String message, String operation, String details, Throwable cause) {
        super(sqlCode, message, operation, details, cause);
    }

    public NonFatalDbException(String sqlCode, String message, String operation, String details) {
        super(sqlCode, message, operation, details);
    }

    public NonFatalDbException(String sqlCode, String operation, String details) {
        super(sqlCode, "Error", operation, details);
    }

    public NonFatalDbException(String sqlCode, String operation, String details, Throwable cause) {
        super(sqlCode, "Error", operation, details, cause);
    }
}
