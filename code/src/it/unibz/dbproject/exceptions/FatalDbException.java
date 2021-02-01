package it.unibz.dbproject.exceptions;

public class FatalDbException extends DbException {
    public FatalDbException(String sqlCode, String message, String operation, String details, Throwable cause) {
        super(sqlCode, message, operation, details, cause);
    }

    public FatalDbException(String sqlCode, String message, String operation, String details) {
        super(sqlCode, message, operation, details);
    }

    public FatalDbException(String sqlCode, String operation, String details) {
        super(sqlCode, "Fatal error", operation, details);
    }

    public FatalDbException(String sqlCode, String operation, String details, Throwable cause) {
        super(sqlCode, "Fatal error", operation, details, cause);
    }
}
