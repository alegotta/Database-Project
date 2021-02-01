package it.unibz.dbproject.exceptions;

public class ConstraintViolatedException extends NonFatalDbException {
    public ConstraintViolatedException(String sqlCode, String message, String operation, String details, Throwable cause) {
        super(sqlCode, message, operation, details, cause);
    }

    public ConstraintViolatedException(String sqlCode, String message, String operation, String details) {
        super(sqlCode, message, operation, details);
    }

    public ConstraintViolatedException(String sqlCode, String operation, String details) {
        super(sqlCode, "Constraint violated", operation, details);
    }

    public ConstraintViolatedException(String sqlCode, String operation, String details, Throwable cause) {
        super(sqlCode, "Constraint violated", operation, details, cause);
    }
}
