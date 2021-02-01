package it.unibz.dbproject.operations.dml;

import it.unibz.dbproject.operations.qf.CalendarFilter;
import it.unibz.dbproject.operations.qf.CompanyFilter;
import it.unibz.dbproject.operations.qf.LineFilter;
import it.unibz.dbproject.operations.qf.StopTimetable;

import java.sql.Types;
import java.util.List;

public class Op7 extends DMLOperation {
    public Op7() {
        super("Add new trip",
                "Add a new trip given the code, the timetable, the operating company and the calendar"
        );
        addPreOperation(new CalendarFilter());
        addPreOperation(new CompanyFilter());
        addPreOperation(new LineFilter());
        addPreOperation(new StopTimetable(Integer.MAX_VALUE));

        inputs.addHiddenParameter(2, "Calendar ID", Types.INTEGER);
        inputs.addHiddenParameter(3, "Line Code", Types.VARCHAR);
        inputs.addHiddenParameter(4, "Line City", Types.VARCHAR);
        inputs.addHiddenParameter(5, "Company Name", Types.VARCHAR);

        inputs.addParameter(1, "the trip code", Types.INTEGER, "");
    }

    @Override
    public String getQuery() {
        int calendarId = ((CalendarFilter.Result) getPreOperationResults().get(0).get(0)).id;
        String companyName = ((CompanyFilter.Result) getPreOperationResults().get(1).get(0)).name;
        LineFilter.Result line = ((LineFilter.Result) getPreOperationResults().get(2).get(0));
        List<StopTimetable.Result> stopTimetable = Op7Timetable.getInstance().timetable;

        inputs.setFirstValue(2, calendarId);
        inputs.setFirstValue(3, line.shortCode);
        inputs.setFirstValue(4, line.cityName);
        inputs.setFirstValue(5, companyName);


        return "INSERT INTO Trip(code,calendar,lineCode,lineCity,company) VALUES (?, ?, ?, ?, ?); " +
                "INSERT INTO StopsAt(latitude,longitude,\"order\",arrivalTime,departureTime,trip) VALUES " + buildTimetableString(stopTimetable, (Integer) inputs.getValues(1).get(0)) + ";";
    }

    private String buildTimetableString(List<StopTimetable.Result> stopTimetable, int tripCode) {
        StringBuilder buf = new StringBuilder();
        int order = 1;

        for(StopTimetable.Result timetable : stopTimetable) {
            buf.append("(");
            buf.append(timetable.latitude).append(",");
            buf.append(timetable.longitude).append(",");
            buf.append(order++).append(",");
            buf.append(timetable.arrivalTime).append(",");
            buf.append(timetable.departureTime).append(",");
            buf.append(tripCode);
            buf.append("),");
        }

        if (buf.length() > 0)
            return buf.substring(0, buf.length()-1);
        else
            return "";
    }
}
