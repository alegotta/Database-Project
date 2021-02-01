package it.unibz.dbproject.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class OperationResult<T> {
    public void add(T result) {
    }
    public void addAll(Collection<T> result) {
    }

    public List<T> get() {
        return new ArrayList<>();
    }

    public void clearValues() {
    }

    public String getString() {
        return "";
    }
}
