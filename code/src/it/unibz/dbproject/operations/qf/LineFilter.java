package it.unibz.dbproject.operations.qf;

import it.unibz.dbproject.operations.ql.DataOut;

import java.sql.Types;

public class LineFilter extends QueryFilterOperation<LineFilter.Result> {
    public LineFilter() {
        super("",
                "Choose a line",
                Result.class
        );
        addFilterParameter(1, "Choose a specific line", Types.INTEGER, "ID");
        addFilterPredicate(line -> line.id == getFilterParameterValue(1).get(0));
    }

    @Override
    public String getQuery() {
        return "SELECT * FROM Line";
    }

    public static class Result extends DataOut {
        public final String shortCode;
        public final String cityName;
        public final String color;
        public final String vehicleType;
        public final String description;

        public Result(int id, String vehicleType, String description, String shortCode, String color, String cityName) {
            super(id);
            this.shortCode = shortCode;
            this.cityName = cityName;
            this.color = color;
            this.vehicleType = vehicleType;
            this.description = description;
        }
    }
}
