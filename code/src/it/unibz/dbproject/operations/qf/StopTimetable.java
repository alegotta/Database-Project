package it.unibz.dbproject.operations.qf;

import it.unibz.dbproject.operations.dml.Op7Timetable;
import it.unibz.dbproject.operations.ql.Op1;

import java.sql.Types;

public class StopTimetable extends QueryFilterOperation<StopTimetable.Result> {
    public StopTimetable() {
        this(1);
    }

    public StopTimetable(int maxCardinality) {
        super("Insert the timetable", "Or type 'q' to terminate", maxCardinality, Result.class, new StopTimetableResult());
        inputs.addParameter(1, "Stop name", Types.VARCHAR, "");
        inputs.addParameter(2, "Language", Types.VARCHAR, "it/de");

        addFilterParameter(1,"Choose the ID", Types.INTEGER, "");
        addFilterParameter(2, "Insert the arrival time, or null", Types.VARCHAR, "HH:MM");
        addFilterParameter(3, "Insert the departure time, or null", Types.VARCHAR, "HH:MM");

        addFilterPredicate(stop -> getFilterParameterValue(1).get(0) == stop.id);
    }

    @Override
    public String getQuery() {
        return "SELECT S.city, SN.value AS name, S.latitude, S.longitude " +
                "FROM Stop S, StopHasName SN " +
                "WHERE S.latitude=SN.latitude AND S.longitude=SN.longitude AND S.platformName IS NULL AND SN.value ILIKE CONCAT('',?,'%') AND SN.language=?";
    }

    @Override
    public void termination() {
        Op7Timetable.getInstance().timetable.addAll(result.get());
    }

    public static class Result extends Op1.Result {
        public String arrivalTime;
        public String departureTime;
        public String trip;

        public Result(int id, String name, String city, double latitude, double longitude) {
            super(id, "'"+name+"'", "'"+city+"'", latitude, longitude);
            this.arrivalTime = null;
            this.departureTime = null;
            this.trip = null;
        }
    }
}
