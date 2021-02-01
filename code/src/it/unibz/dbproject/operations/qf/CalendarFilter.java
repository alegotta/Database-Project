package it.unibz.dbproject.operations.qf;

import it.unibz.dbproject.operations.ql.DataOut;

import java.sql.Date;
import java.sql.Types;

public class CalendarFilter extends QueryFilterOperation<CalendarFilter.Result> {
    public CalendarFilter() {
        super("",
                "Choose a calendar",
                Result.class
        );
        addFilterParameter(1, "Choose a specific calendar", Types.INTEGER, "ID");
        addFilterPredicate(calendar -> calendar.id == getFilterParameterValue(1).get(0));
    }

    @Override
    public String getQuery() {
        return "SELECT sunday,saturday,endDate,tuesday,wednesday,thursday,friday,startDate,monday FROM Calendar";
    }

    public static class Result extends DataOut {
        public final Date startDate;
        public final Date endDate;
        public final boolean monday;
        public final boolean tuesday;
        public final boolean wednesday;
        public final boolean thursday;
        public final boolean friday;
        public final boolean saturday;
        public final boolean sunday;

        public Result(int id, boolean sunday, boolean saturday, Date endDate, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, Date startDate,  boolean monday) {
            super(id);
            this.startDate = startDate;
            this.endDate = endDate;
            this.monday = monday;
            this.tuesday = tuesday;
            this.wednesday = wednesday;
            this.thursday = thursday;
            this.friday = friday;
            this.saturday = saturday;
            this.sunday = sunday;
        }
    }
}
