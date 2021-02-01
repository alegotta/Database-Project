package it.unibz.dbproject.operations.qf;

import it.unibz.dbproject.operations.ql.Op1;

import java.sql.Types;

public class StopNameFilter extends QueryFilterOperation<Op1.Result> {
    public StopNameFilter() {
        this(1);
    }

    public StopNameFilter(int maxCardinality) {
        super(new Op1());

        addFilterParameter(1,"Choose a specific stop", Types.INTEGER, "ID");
        addFilterPredicate(stop -> {
            int id = (Integer) getFilterParameterValue(1).get(0);
            return id==stop.id;
        });
    }
}
