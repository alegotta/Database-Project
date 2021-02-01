package it.unibz.dbproject.operations.ql;

import it.unibz.dbproject.operations.qf.StopNameFilter;

import java.sql.Time;
import java.sql.Types;

public class Op4 extends QueryOperation<Op4.Result> {
    public Op4() {
        super("Trips between two stops",
                "Find the next ten trips departing from one stop and arriving at another one, at a given date and time",
                Result.class
        );
        addPreOperation(new StopNameFilter());
        addPreOperation(new StopNameFilter());

        inputs.addHiddenParameter(1, "first stop latitude", Types.FLOAT);
        inputs.addHiddenParameter(2, "first stop longitude", Types.FLOAT);
        inputs.addHiddenParameter(3, "second stop latitude", Types.FLOAT);
        inputs.addHiddenParameter(4, "second stop longitude", Types.FLOAT);
        inputs.addParameter(6, "the date", Types.DATE, "YYYY-MM-DD");
        inputs.addParameter(5, "the time", Types.TIME, "HH:MM");
    }

    @Override
    public String getQuery() {
        inputs.addValue(1, ((Op1.Result) getPreOperationResults().get(0).get(0)).latitude);
        inputs.addValue(2, ((Op1.Result) getPreOperationResults().get(0).get(0)).longitude);
        inputs.addValue(3, ((Op1.Result) getPreOperationResults().get(1).get(0)).latitude);
        inputs.addValue(4, ((Op1.Result) getPreOperationResults().get(1).get(0)).longitude);
        inputs.copyValue(6,7);

        return "SELECT T.company, L.shortCode AS Line, L.vehicleType, T.code as \"Trip Code\", S1.departureTime AS \"Departure Time\", S2.arrivalTime AS \"Arrival Time\", (S2.arrivalTime-S1.departureTime) AS duration " +
                "FROM trip T, calendar C, stopsAt S1, stopsAt S2, line L " +
                "WHERE S1.latitude=? AND S1.longitude=? AND S2.latitude=? AND S2.longitude=? AND S1.departureTime>=? " +
                "AND S1.trip=S2.trip AND T.calendar=C.ID AND T.code=S1.trip AND T.lineCode=L.shortCode " +
                "AND (((C.monday=True) AND (? NOT IN (SELECT date FROM HasExceptionOn E WHERE suppressed=True AND E.calendar=C.ID))) OR (? IN (SELECT date FROM HasExceptionOn E WHERE suppressed=False AND E.calendar=C.ID))) " +
                "ORDER BY S1.arrivalTime " +
                "LIMIT 10";
    }

    static class Result extends DataOut {
        public final String company;
        public final String line;
        public final String vehicle_type;
        public final int trip_code;
        public final Time departure_time;
        public final Time arrival_time;
        public final String duration;

        public Result(int id, String vehicle_type, String duration, Time arrival_time, int trip_code, String line, String company, Time departure_time) {
            super(id);
            this.company = company;
            this.line = line;
            this.vehicle_type = vehicle_type;
            this.trip_code = trip_code;
            this.departure_time = departure_time;
            this.arrival_time = arrival_time;
            this.duration = duration;
        }
    }
}
