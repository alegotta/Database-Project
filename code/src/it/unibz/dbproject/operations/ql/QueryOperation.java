package it.unibz.dbproject.operations.ql;

import it.unibz.dbproject.Db;
import it.unibz.dbproject.exceptions.InstantiationException;
import it.unibz.dbproject.operations.Operation;
import it.unibz.dbproject.operations.OperationResult;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class QueryOperation<T> extends Operation<T> {
    public final Class<T> returnClass;

    public QueryOperation(String name, String description, Class<T> returnClass) {
        this(name,description, returnClass, 1, new QueryOperationResult<>());
    }
    public QueryOperation(String name, String description, int maxCardinality, Class<T> returnClass) {
        this(name,description, returnClass, maxCardinality, new QueryOperationResult<>());
    }
    public QueryOperation(String name, String description, Class<T> returnClass, int maxCardinality, OperationResult<T> result) {
        super(name, description, maxCardinality, result);
        this.returnClass = returnClass;
    }


    @Override
    protected void executeInternal() {
        List<Map<String,Object>> dbResult = Db.getInstance().select(getQuery(), inputs.getInputs());
        result.addAll(createObjects(dbResult));
    }

    @SuppressWarnings("unchecked")
    public List<T> createObjects(List<Map<String,Object>> results) {
        List<T> elements = new ArrayList<>();

        if (returnClass.getConstructors().length==0 || returnClass.getConstructors()[0].getParameters().length==0 || results.size()==0)
            return elements;
        else if (returnClass.getConstructors()[0].getParameters().length-1 != results.get(0).size())
            throw new InstantiationException("Number of parameters mismatched when creating objects");
        else {
            Constructor<T> primaryConstructor = (Constructor<T>) returnClass.getConstructors()[0];

            int id = 1;
            for (Map<String, Object> res : results) {
                Collection<Object> values = new ArrayList<>();
                values.add(id);
                values.addAll(res.values());

                try {
                    elements.add(primaryConstructor.newInstance(values.toArray()));
                    id++;
                } catch (Exception e) {
                    throw new InstantiationException("Type mismatch when creating objects", e);
                }
            }
        }
        return elements;
    }
}
