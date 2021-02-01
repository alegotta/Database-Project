package it.unibz.dbproject.exceptions;

public class NotFoundException extends NonFatalDbException {
    public NotFoundException(String sqlCode, String message, String operation, String details, Throwable cause) {
        super(sqlCode, message, operation, details, cause);
    }

    public NotFoundException(String sqlCode, String message, String operation, String details) {
        super(sqlCode, operation, details, message);
    }

    public NotFoundException(String sqlCode, String operation, String details) {
        super(sqlCode, "Object not found", operation, details);
    }

    public NotFoundException(String sqlCode, String operation, String details, Throwable cause) {
        super(sqlCode, "Object not found", operation, details, cause);
    }
}
