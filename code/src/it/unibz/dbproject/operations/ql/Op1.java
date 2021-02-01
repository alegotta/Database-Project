package it.unibz.dbproject.operations.ql;

import java.sql.Types;

public class Op1 extends QueryOperation<Op1.Result> {
    public Op1() {
        super("Stops by name",
                " Get a list of ten stops with name in a specific language matching the user-given string",
                Result.class
        );

        inputs.addParameter(1, "the stop name", Types.VARCHAR, "");
        inputs.addParameter(2, "the language", Types.VARCHAR, "it/de");
    }

    @Override
    public String getQuery() {
        return "SELECT S.city, SN.value AS name, S.latitude, S.longitude " +
                "FROM Stop S, StopHasName SN " +
                "WHERE S.latitude=SN.latitude AND S.longitude=SN.longitude AND S.platformName IS NULL AND SN.value ILIKE CONCAT('',?,'%') AND SN.language=?";
    }

    public static class Result extends DataOut {
        public final String city;
        public final String name;
        public final Double latitude;
        public final Double longitude;

        public Result(int id, String name, String city, double latitude, double longitude) {
            super(id);
            this.latitude = latitude;
            this.longitude = longitude;
            this.city = city;
            this.name = name;
        }
    }
}
