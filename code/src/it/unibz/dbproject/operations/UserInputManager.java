package it.unibz.dbproject.operations;

import java.util.*;
import java.util.stream.Collectors;

public class UserInputManager {
    private final Map<Integer, UserInput<?>> parameters;

    public UserInputManager() {
        this.parameters = new HashMap<>();
    }

    public void addParameter(int index, String description, int dataType, String typeDesc) {
        addParameter(index, description, dataType, typeDesc, true);
    }
    public void addHiddenParameter(int index, String description, int dataType) {
        addParameter(index, description, dataType, "", false);
    }
    private <T> void addParameter(int index, String description, int dataType, String typeDesc, boolean userVisible) {
        UserInput<T> in = new UserInput<>(dataType, description, typeDesc, userVisible);
        parameters.put(index, in);
    }
    public void addAllParameters(UserInputManager manager) {
        parameters.putAll(manager.parameters);
    }

    public void copyValue(int originalIndex, int newIndex) {
        UserInput<?> original = parameters.get(originalIndex);
        if (original != null) {
            UserInput<?> inp = new UserInput<>(original.getDataType(), original.getInputDesc(), original.getTypeDesc(), false);
            inp.addAllValues((List<Object>) original.getValues());
            parameters.put(newIndex, inp);
        }
    }

    public void addValue(int index, String value) {
        if (parameters.get(index)!=null) parameters.get(index).addValue(value);
    }
    public void setFirstValue(int index, String value) {
        if (parameters.get(index)!=null) parameters.get(index).setFirstValue(value);
    }
    public <T> void addValue(int index, T value) {
        if (parameters.get(index)!=null) parameters.get(index).addValue(value);
    }
    public <T> void setFirstValue(int index, T value) {
        if (parameters.get(index)!=null) parameters.get(index).setFirstValue(value);
    }
    public void setInputTypeDesc(int index, String desc) {
        if (parameters.get(index)!=null) parameters.get(index).setTypeDesc(desc);
    }

    public List<?> getValues(int idx) {
        if (parameters.get(idx)!=null) return parameters.get(idx).getValues();
        else return Collections.emptyList();
    }

    public List<List<?>> getValues() {
        /*for(UserInput<?> in : parameters.values()) {
            if (in.getMinCardinality() < in.getValues().size())
                throw new MalformedParameterException("Cardinality checking", "The minimum cardinality constraint of " + in.getMinCardinality() + " is not satisfied for input " + in.getInputDesc());
            else if (in.getValues().size() > in.getMaxCardinality())
                throw new MalformedParameterException("Cardinality checking", "The maximum cardinality constraint of " + in.getMaxCardinality() + " is not satisfied for input " + in.getInputDesc());
        }*/

        return parameters.values().parallelStream().map(UserInput::getValues).collect(Collectors.toList());
    }

    public List<?> getSingleValues() {
        return getValues().parallelStream()
                .map(e -> e.get(0))
                .collect(Collectors.toList());
    }

    public Collection<UserInput<?>> getInputs() {
        return parameters.values();
    }

    public ListIterator<UserInput<?>> userIterator() {
        return parameters.values().parallelStream()
                .filter(UserInput::isUserVisible)
                .collect(Collectors.toList())
                .listIterator();
    }

    public void clearValues() {
        for(UserInput<?> inp : parameters.values()) {
            inp.clearValues();
        }
    }
}
