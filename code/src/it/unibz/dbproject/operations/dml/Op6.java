package it.unibz.dbproject.operations.dml;

import it.unibz.dbproject.operations.qf.CityNamesFilter;

import java.sql.Types;

public class Op6 extends DMLOperation {
    public Op6() {
        super("Add new line",
                "Add a new line given short code, colour, type of vehicle and city"
        );
        addPreOperation(new CityNamesFilter());

        inputs.addParameter(1, "the short Code", Types.VARCHAR, "");
        inputs.addParameter(2, "the color", Types.VARCHAR, "");
        inputs.addParameter(3, "the vehicle type", Types.OTHER, "");
        inputs.addParameter(5, "the description", Types.OTHER, "");
        inputs.addHiddenParameter(4, "the departing city name", Types.VARCHAR);
    }

    @Override
    public String getQuery() {
        inputs.addValue(4, ((CityNamesFilter.Result) getPreOperationResults().get(0).get(0)).name);

        return "INSERT INTO Line(shortCode, color, vehicleType, departingcity, description) VALUES (?, ?, ?, ?, ?)";
    }
}
