package it.unibz.dbproject.operations.dml;

import it.unibz.dbproject.operations.qf.CityNamesFilter;

import java.sql.Types;

public class Op5 extends DMLOperation {
    public Op5() {
        super("Add new company",
                "Add a new company given the name and the headquarter city"
        );
        addPreOperation(new CityNamesFilter());

        inputs.addParameter(1, "the name of the company", Types.VARCHAR, "");
        inputs.addHiddenParameter(2, "the name of the headquarter city", Types.VARCHAR);
    }

    @Override
    public String getQuery() {
        inputs.addValue(2, ((CityNamesFilter.Result) getPreOperationResults().get(0).get(0)).name);

        return "INSERT INTO Company VALUES(?, ?)";
    }
}
