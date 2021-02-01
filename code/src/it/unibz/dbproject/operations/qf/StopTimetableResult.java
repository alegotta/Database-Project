package it.unibz.dbproject.operations.qf;

import java.util.List;

public class StopTimetableResult extends QueryFilterResult<StopTimetable.Result> {
    @Override
    public List<StopTimetable.Result> get() {
        List<StopTimetable.Result> results = super.get();
        List<?> arrivalTimes = filterParameters.getValues(2);
        List<?> departureTimes = filterParameters.getValues(3);

        for(int i=0; i<results.size() && i<arrivalTimes.size(); i++) {
            StopTimetable.Result stopTimetable = results.get(i);
            String arrivalTime = (String) arrivalTimes.get(i);
            String departureTime = (String) departureTimes.get(i);

            stopTimetable.arrivalTime = (arrivalTime==null) ? null : "'"+arrivalTime+":00'";
            stopTimetable.departureTime = (departureTime==null) ? null : "'"+departureTime+":00'";
        }
        return results;
    }
}
