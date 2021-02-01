package it.unibz.dbproject.operations.dml;

import it.unibz.dbproject.Db;
import it.unibz.dbproject.operations.Operation;

public abstract class DMLOperation extends Operation<Integer> {
    public DMLOperation(String name, String description) {
        super(name, description, 1, new DMLResult());
    }

    @Override
    protected void executeInternal() {
        Integer affectedRows = Db.getInstance().insert(getQuery(), inputs.getInputs());
        result.add(affectedRows);
    }
}
