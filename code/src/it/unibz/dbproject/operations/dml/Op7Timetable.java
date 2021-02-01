package it.unibz.dbproject.operations.dml;

import it.unibz.dbproject.operations.qf.StopTimetable;

import java.util.ArrayList;
import java.util.List;

public class Op7Timetable {
    private static Op7Timetable singleton;

    public final List<StopTimetable.Result> timetable;

    private Op7Timetable() {
        this.timetable = new ArrayList<>();
    }

    public static Op7Timetable getInstance() {
        if (singleton==null)
            singleton = new Op7Timetable();
        return singleton;
    }
}
