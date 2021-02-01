package it.unibz.dbproject.exceptions;

public class TypeCastException extends NonFatalDbException {
    public TypeCastException(String sqlCode, String message, String operation, String details, Throwable cause) {
        super(sqlCode, message, operation, details, cause);
    }

    public TypeCastException(String sqlCode, String message, String operation, String details) {
        super(sqlCode, message, operation, details);
    }

    public TypeCastException(String sqlCode, String operation, String details) {
        super(sqlCode, "Type cast error", operation, details);
    }

    public TypeCastException(String sqlCode, String operation, String details, Throwable cause) {
        super(sqlCode, "Type cast error", operation, details, cause);
    }
}
