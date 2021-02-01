package it.unibz.dbproject.operations.qf;

import it.unibz.dbproject.exceptions.MalformedParameterException;
import it.unibz.dbproject.operations.UserInput;
import it.unibz.dbproject.operations.UserInputManager;
import it.unibz.dbproject.operations.ql.QueryOperationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class QueryFilterResult<T> extends QueryOperationResult<T> {
    protected final List<Predicate<T>> filterPredicates;
    protected final UserInputManager filterParameters;

    public QueryFilterResult() {
        this.filterParameters = new UserInputManager();
        this.filterPredicates = new ArrayList<>();
    }

    public ListIterator<UserInput<?>> getUserIterator() {
        return filterParameters.userIterator();
    }

    @Override
    public List<T> get() {
        List<T> filteredResults = new ArrayList<>(filterResults());
        clearValues();
        addAll(filteredResults);

        return super.get();
    }

    private List<T> filterResults() {
        List<T> originalResults = super.get();
        if (originalResults.size() == 0 || filterParameters.getValues().stream().anyMatch(List::isEmpty))
            return originalResults;

        List<T> filteredResults = originalResults.parallelStream()
                .filter(filterPredicates.stream().reduce(x->true, Predicate::and))
                .collect(Collectors.toList());

        if (filteredResults.size() == 0)
            throw new MalformedParameterException("Object filtering", "The chosen ID " + filterParameters.getValues() + " is not valid");

        return filteredResults;
    }
}
