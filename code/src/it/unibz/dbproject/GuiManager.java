package it.unibz.dbproject;

import it.unibz.dbproject.operations.Operation;
import it.unibz.dbproject.operations.exit.ExitOperation;
import it.unibz.dbproject.operations.UserInput;
import it.unibz.dbproject.operations.dml.Op5;
import it.unibz.dbproject.operations.dml.Op6;
import it.unibz.dbproject.operations.dml.Op7;
import it.unibz.dbproject.operations.dml.Op8;
import it.unibz.dbproject.operations.qf.QueryFilterOperation;
import it.unibz.dbproject.operations.qf.QueryFilterResult;
import it.unibz.dbproject.operations.ql.Op1;
import it.unibz.dbproject.operations.ql.Op2;
import it.unibz.dbproject.operations.ql.Op3;
import it.unibz.dbproject.operations.ql.Op4;

import java.util.*;

public class GuiManager {
    private final List<Operation<?>> operations;

    private Operation<?> curMainOperation;
    private Operation<?> curSubOperation;
    private ListIterator<UserInput<?>> curIterator;
    private ListIterator<UserInput<?>> curFilter;
    private boolean isOp7;

    public GuiManager() {
        Operation<?> exitOperation = new ExitOperation();

        this.operations = new ArrayList<>(9);
        this.curSubOperation = exitOperation;
        this.curMainOperation = exitOperation;
        this.curIterator = Collections.emptyListIterator();
        this.curFilter = Collections.emptyListIterator();
        this.isOp7 = false;

        addOperation(new Op1(), new Op2(), new Op3(), new Op4(), new Op5(), new Op6(), new Op7(), new Op8());
        addOperation(exitOperation);
    }

    private void addOperation(Operation<?> operation) {
        operations.add(operation);
    }

    private void addOperation(Operation<?>... operations) {
        this.operations.addAll(Arrays.asList(operations));
    }

    public String getMenu() {
        StringBuilder buf = new StringBuilder();
        buf.append("------------ MENU ------------\n");
        for(int i=0; i<operations.size(); i++)
            buf.append(i+1).append(") ").append(operations.get(i).name).append("\n");

        buf.append("------------------------------");

        return buf.toString();
    }

    public void chooseOperation(int index) {
        if (index-1<operations.size()) {
            curMainOperation = operations.get(index-1);
            curSubOperation = null;
            curIterator = curMainOperation.inputs.userIterator();
        }
    }

    public String getOperationMenu() {
        StringBuilder buf = new StringBuilder();
        Operation<?> operation = nextSubOperation();

        if (!operation.name.isBlank())
            buf.append("------ ").append(operation.name).append(" ------\n");
        buf.append(operation.description);

        return buf.toString();
    }

    public boolean hasNextSubOperation() {
        return curSubOperation != curMainOperation;
    }

    private Operation<?> nextSubOperation() {
        int lastPreOpIdx = curMainOperation.preOperations.indexOf(curSubOperation);

        if (isOp7)
            curSubOperation = curMainOperation;
        else if (curSubOperation == curMainOperation)
            throw new IllegalStateException("No operations to be done");
        else if (curSubOperation == null)
            curSubOperation = (curMainOperation.preOperations.size()==0) ? curMainOperation : curMainOperation.preOperations.get(0);
        else if (lastPreOpIdx+1 == curMainOperation.preOperations.size()) {
            if (curSubOperation.maxCardinality == 1)
                curSubOperation = curMainOperation;
        } else
            curSubOperation = curMainOperation.preOperations.get(lastPreOpIdx+1);

        curIterator = curSubOperation.inputs.userIterator();
        if (curSubOperation instanceof QueryFilterOperation)
            curFilter = ((QueryFilterResult<?>) curSubOperation.result).getUserIterator();
        else
            curFilter = Collections.emptyListIterator();

        return curSubOperation;
    }

    public boolean hasNextInput() {
        return curIterator.hasNext();
    }
    public String getInputName() {
        if (curIterator.hasNext()) {
            UserInput<?> inp = curIterator.next();
            return inp.getInputDesc() + inp.getTypeDesc();
        }
        else
            return "null";
    }
    public void setOperationInput(String value) {
        insertAndCheckForQ(value, curIterator);
    }


    public boolean hasNextFilter() {
        boolean bol = curFilter.hasNext();
        if (!bol)
            curSubOperation.termination();

        return bol;
    }
    public String getFilterName() {
        if (curFilter.hasNext()) {
            UserInput<?> inp = curFilter.next();
            return inp.getInputDesc() + inp.getTypeDesc();
        }
        else
            return "null";
    }
    public void setOperationFilter(String value) {
        insertAndCheckForQ(value, curFilter);
    }

    private void insertAndCheckForQ(String value, ListIterator<UserInput<?>> it) {
        if (value.equalsIgnoreCase("q")) {
            curFilter = Collections.emptyListIterator();
            curIterator = Collections.emptyListIterator();
            isOp7 = true;
        } else {
            UserInput<?> inp = it.previous();
            inp.addValue(value);
            it.next();
        }
    }

    public String executeOperation() {
        curSubOperation.execute();
        curSubOperation.result.get();
        return curSubOperation.result.getString();
    }

    public int getExitIndex() {
        Operation<?> exitOperation = operations.parallelStream()
                .filter(e -> e instanceof ExitOperation)
                .findFirst()
                .orElse(new ExitOperation());

        return operations.indexOf(exitOperation)+1;
    }

    public int getLastIndex() {
        return operations.size();
    }
}
