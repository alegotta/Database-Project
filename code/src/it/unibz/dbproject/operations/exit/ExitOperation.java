package it.unibz.dbproject.operations.exit;

import it.unibz.dbproject.operations.Operation;

public class ExitOperation extends Operation<Object> {
    public ExitOperation() {
        super("Exit", "Terminating...", 1, new ExitResult());
    }

    @Override
    public void executeInternal() {
    }

    @Override
    public String getQuery() {
        return "";
    }
}
