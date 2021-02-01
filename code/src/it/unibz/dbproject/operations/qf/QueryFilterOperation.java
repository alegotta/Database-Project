package it.unibz.dbproject.operations.qf;

import it.unibz.dbproject.operations.OperationResult;
import it.unibz.dbproject.operations.ql.QueryOperation;

import java.util.List;
import java.util.function.Predicate;

public abstract class QueryFilterOperation<T> extends QueryOperation<T> {
    private QueryOperation<T> referenceOperation;

    public QueryFilterOperation(QueryOperation<T> operation) {
        this(operation.name, operation.description, operation.returnClass);
        this.referenceOperation = operation;
        inputs.addAllParameters(operation.inputs);
    }

    public QueryFilterOperation(String name, String description, int maxCardinality, Class<T> returnClass, OperationResult<T> result) {
        super(name, description, returnClass, maxCardinality, result);
        this.referenceOperation = null;
    }

    public QueryFilterOperation(String name, String description, Class<T> returnClass) {
        super(name, description, returnClass, 1, new QueryFilterResult<>());
    }

    protected void addFilterParameter(int index, String description, int dataType, String typeDesc) {
        ((QueryFilterResult<T>) result).filterParameters.addParameter(index, description, dataType, typeDesc);
    }
    protected List<?> getFilterParameterValue(int index) {
        return ((QueryFilterResult<T>) result).filterParameters.getValues(index);
    }

    protected void addFilterPredicate(Predicate<T> predicate) {
        ((QueryFilterResult<T>) result).filterPredicates.add(predicate);
    }

    @Override
    public String getQuery() {
        if (referenceOperation!=null)
            return referenceOperation.getQuery();
        else
            return "";
    }

    public void execute() {
        super.execute();
        ((QueryFilterResult<T>) result).filterParameters.clearValues();
    }
}
