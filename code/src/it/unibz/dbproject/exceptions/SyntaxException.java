package it.unibz.dbproject.exceptions;

public class SyntaxException extends NonFatalDbException {
    public SyntaxException(String sqlCode, String message, String operation, String details, Throwable cause) {
        super(sqlCode, message, operation, details, cause);
    }

    public SyntaxException(String sqlCode, String message, String operation, String details) {
        super(sqlCode, message, operation, details);
    }

    public SyntaxException(String sqlCode, String operation, String details) {
        super(sqlCode, "Wrong syntax in SQL query", operation, details);
    }

    public SyntaxException(String sqlCode, String operation, String details, Throwable cause) {
        super(sqlCode, "Wrong syntax in SQL query", operation, details, cause);
    }
}
