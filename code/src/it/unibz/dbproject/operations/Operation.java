package it.unibz.dbproject.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Operation<T> {
    public final String name;
    public final String description;
    public final int maxCardinality;
    public final UserInputManager inputs;
    public final OperationResult<T> result;
    public final List<Operation<?>> preOperations;


    public Operation(String name, String description, int maxCardinality, OperationResult<T> result) {
        this.name = name;
        this.description = description;
        this.maxCardinality = maxCardinality;
        this.result = result;
        this.inputs = new UserInputManager();
        this.preOperations = new ArrayList<>();
    }

    protected void addPreOperation(Operation<?> operation) {
        if (operation!=null)
            preOperations.add(operation);
    }

    public void execute() {
        result.clearValues();
        executeInternal();
        inputs.clearValues();
    }

    protected abstract void executeInternal();

    public abstract String getQuery();

    protected List<List<?>> getPreOperationResults() {
        return preOperations.parallelStream()
                .map(op -> op.result.get())
                .collect(Collectors.toList());
    }

    public void termination() {
    }
}
