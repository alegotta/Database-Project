package it.unibz.dbproject.exceptions;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;

public class DbException extends RuntimeException {
    private final String sqlState;
    private final String operation;
    private final String details;

    public DbException(String sqlState, String message, String operation, String details, Throwable cause) {
        super(message, cause);
        this.sqlState = sqlState;
        this.operation = operation;
        this.details = details;
    }

    public DbException(String sqlState, String message, String operation, String details) {
        this(sqlState, message, operation, details, null);
    }

    public DbException(String sqlState, String operation, String details) {
        this(sqlState, "Database error " + sqlState, operation, details);
    }

    public DbException(String sqlState, String operation, String details, Throwable cause) {
        this(sqlState, details, operation, "", cause);
    }

    public String getSqlState() {
        return sqlState;
    }

    public String getOperation() {
        return operation;
    }

    public String getDetails() {
        return details;
    }

    public static void handleException(SQLException e, String operation) {
        String sqlState = e.getSQLState();
        String details = e.getMessage();

        if (sqlState == null)
            throw new DbException("", operation, details, e);
        if (sqlState.startsWith("23"))
            throw new ConstraintViolatedException(sqlState, operation, details, e);
        else if (sqlState.startsWith("42"))
            throw new SyntaxException(sqlState, operation, details, e);
        if (e instanceof SQLTimeoutException || sqlState.startsWith("08"))
            throw new ConnectionException(sqlState, operation, details, e);
        else if (sqlState.startsWith("28"))
            throw new PrivilegeException(sqlState, operation, details, e);
        else if (sqlState.startsWith("3D"))
            throw new NotFoundException(sqlState, operation, details, e);
        else if (sqlState.startsWith("999"))
            throw new TypeCastException(sqlState, operation, details, e);
        else if (sqlState.startsWith("22"))
            throw new MalformedParameterException(sqlState, operation, details, e);
        else if (sqlState.startsWith("P0001"))
            throw new ConstraintViolatedException(sqlState, operation, details, e);
        else
            throw new DbException(sqlState, operation, details, e);
    }
}
