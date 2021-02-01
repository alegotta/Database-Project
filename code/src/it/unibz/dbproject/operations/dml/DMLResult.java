package it.unibz.dbproject.operations.dml;

import it.unibz.dbproject.operations.OperationResult;

public class DMLResult extends OperationResult<Integer> {
    private int affectedRows;

    public DMLResult() {
        this.affectedRows = 0;
    }

    @Override
    public void add(Integer affectedRows) {
        this.affectedRows = affectedRows;
    }

    @Override
    public String getString() {
        if (affectedRows==0)
            return "No row affected";
        else if (affectedRows==1)
            return "One row affected";
        else
            return affectedRows + " rows affected";
    }
}
