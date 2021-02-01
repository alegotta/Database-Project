package it.unibz.dbproject.exceptions;

public class PrivilegeException extends FatalDbException {
    public PrivilegeException(String sqlCode, String message, String operation, String details, Throwable cause) {
        super(sqlCode, message, operation, details, cause);
    }

    public PrivilegeException(String sqlCode, String message, String operation, String details) {
        super(sqlCode, operation, details, message);
    }

    public PrivilegeException(String sqlCode, String operation, String details) {
        super(sqlCode, "Action forbidden", operation, details);
    }

    public PrivilegeException(String sqlCode, String operation, String details, Throwable cause) {
        super(sqlCode, "Action forbidden", operation, details, cause);
    }
}
