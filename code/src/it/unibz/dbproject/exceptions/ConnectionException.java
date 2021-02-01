package it.unibz.dbproject.exceptions;

public class ConnectionException extends FatalDbException {
    public ConnectionException(String sqlCode, String message, String operation, String details, Throwable cause) {
        super(sqlCode, message, operation, details, cause);
    }

    public ConnectionException(String sqlCode, String message, String operation, String details) {
        super(sqlCode, message, operation, details);
    }

    public ConnectionException(String sqlCode, String operation, String details) {
        super(sqlCode, "Connection timeout", operation, details);
    }

    public ConnectionException(String sqlCode, String operation, String details, Throwable cause) {
        super(sqlCode, "Connection timeout", operation, details, cause);
    }
}
