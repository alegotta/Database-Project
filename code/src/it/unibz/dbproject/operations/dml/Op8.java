package it.unibz.dbproject.operations.dml;

import it.unibz.dbproject.operations.qf.CalendarFilter;

import java.sql.Types;

public class Op8 extends DMLOperation {
    public Op8() {
        super("Modify calendar schedule",
                "Modify the schedule for a calendar knowing its code (add one exceptional date)"
        );
        addPreOperation(new CalendarFilter());

        inputs.addParameter(1, "the exceptional date", Types.DATE, "YYYY-MM-DD");
        inputs.addHiddenParameter(2, "Calendar ID", Types.INTEGER);
        inputs.addParameter(3, "Is the service suppressed?", Types.BOOLEAN, "true=yes,false=no");
    }

    @Override
    public String getQuery() {
        inputs.setFirstValue(2, ((CalendarFilter.Result) getPreOperationResults().get(0).get(0)).id);

        return "INSERT INTO HasExceptionOn VALUES (?, ?, ?)";
    }
}
