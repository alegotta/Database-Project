package it.unibz.dbproject.operations.qf;

import it.unibz.dbproject.operations.ql.DataOut;

import java.sql.Types;

public class CityNamesFilter extends QueryFilterOperation<CityNamesFilter.Result> {
    public CityNamesFilter() {
        super("",
              "Choose the name of a city",
                Result.class
        );
        addFilterParameter(1, "Select a specific city", Types.INTEGER, "ID");
        addFilterPredicate(city -> city.id == getFilterParameterValue(1).get(0));
    }

    @Override
    public String getQuery() {
        return "SELECT * FROM City";
    }

    public static class Result extends DataOut {
        public String name;
        public double latitude;
        public double longitude;

        public Result(int id, String name, double latitude, double longitude) {
            super(id);
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
