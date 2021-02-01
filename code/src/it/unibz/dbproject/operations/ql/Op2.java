package it.unibz.dbproject.operations.ql;

import java.sql.Types;

public class Op2 extends QueryOperation<Op2.Result> {
    public Op2() {
        super("Stops by coordinates",
                " Get a list of ten stops with name and city in a specific language that that are that are contained in a set of coordinates",
                Result.class
        );
        inputs.addParameter(1, "the latitude", Types.FLOAT, "");
        inputs.addParameter(2, "the longitude", Types.FLOAT, "");
        inputs.addParameter(4, "the radius", Types.INTEGER, "meters");
        inputs.addParameter(5, "the language", Types.VARCHAR, "it/de");
    }

    @Override
    public String getQuery() {
        inputs.copyValue(1, 3);

        //Credits to https://gis.stackexchange.com/questions/31628/find-features-within-given-coordinates-and-distance-using-mysql
        return "SELECT S.city, SN.value AS name, S.latitude, S.longitude " +
                "FROM Stop S, StopHasName SN " +
                "WHERE SN.latitude=S.latitude AND SN.longitude=S.longitude " +
                "AND acos( cos(radians(?)) * cos(radians(S.latitude)) * cos(radians(S.longitude) - radians(?)) + sin(radians(?)) * sin(radians(S.latitude)) ) " +
                " * 6371393 <= ? " +
                "AND S.platformName IS NULL AND SN.language LIKE CONCAT('%',?,'%')";
    }

    static class Result extends DataOut {
        public final String city;
        public final String name;
        public final double latitude;
        public final double longitude;

        public Result(int id, String name, String city, double latitude, double longitude) {
            super(id);
            this.latitude = latitude;
            this.longitude = longitude;
            this.city = city;
            this.name = name;
        }
    }
}
