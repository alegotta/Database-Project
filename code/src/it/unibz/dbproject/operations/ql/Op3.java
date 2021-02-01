package it.unibz.dbproject.operations.ql;

import it.unibz.dbproject.operations.qf.StopNameFilter;

import java.sql.Date;
import java.sql.Time;
import java.sql.Types;

public class Op3 extends QueryOperation<Op3.Result> {
    public Op3() {
        super("Trips per Stop",
                "Find the next ten trips, with line and company information, that depart from a specific stop at a given date and time",
                Result.class
        );
        addPreOperation(new StopNameFilter());

        inputs.addHiddenParameter(1, "the stop Latitude", Types.FLOAT);
        inputs.addHiddenParameter(2, "the stop Longitude", Types.FLOAT);
        inputs.addParameter(4,"the date", Types.DATE, "YYYY-MM-DD");
        inputs.addParameter(3,"the time", Types.TIME, "HH:MM");
    }

    @Override
    public String getQuery() {
        Op1.Result stop = ((Op1.Result) getPreOperationResults().get(0).get(0));

        Date date = (Date) inputs.getValues(4).get(0);
        inputs.addValue(1, stop.latitude);
        inputs.addValue(2, stop.longitude);
        inputs.copyValue(4, 5);

        return "SELECT T.company, L.shortCode AS Line, L.vehicleType, T.code as \"Trip Code\", SA.departureTime AS \"Departure Time\" " +
                "FROM trip T, calendar C, stopsAt SA, line L " +
                "WHERE SA.latitude=? AND SA.longitude=? AND SA.departureTime>=? " +
                "AND T.calendar=C.ID AND T.code=SA.trip AND T.lineCode=L.shortCode " +
                "AND (((C."+getDowQuery(date)+"=True) AND (? NOT IN (SELECT date FROM HasExceptionOn E WHERE suppressed=True AND E.calendar=C.ID))) OR (? IN (SELECT date FROM HasExceptionOn E WHERE suppressed=False AND E.calendar=C.ID))) " +
                "ORDER BY SA.arrivalTime " +
                "LIMIT 10";
    }

    private String getDowQuery(Date dow) {
        int d = dow.toLocalDate().getDayOfWeek().getValue();

        return switch (d) {
            case 7 -> "sunday";
            case 1 -> "monday";
            case 2 -> "tuesday";
            case 3 -> "wednesday";
            case 4 -> "thursday";
            case 5 -> "friday";
            case 6 -> "saturday";
            default -> "error";
        };
    }

    static class Result extends DataOut {
        public final String company;
        public final String line;
        public final String vehicle_type;
        public final int trip_code;
        public final Time departure_time;

        public Result(int id, String vehicle_type, String company, int trip_code, Time departure_time, String line) {
            super(id);
            this.company = company;
            this.line = line;
            this.vehicle_type = vehicle_type;
            this.trip_code = trip_code;
            this.departure_time = departure_time;
        }
    }
}
