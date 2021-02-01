package it.unibz.dbproject.operations.qf;

import it.unibz.dbproject.operations.ql.DataOut;

import java.sql.Types;

public class CompanyFilter extends QueryFilterOperation<CompanyFilter.Result> {
    public CompanyFilter() {
        super("",
                "Choose a company",
                Result.class
        );
        addFilterParameter(1, "Choose a specific company", Types.INTEGER, "ID");
        addFilterPredicate(line -> line.id == getFilterParameterValue(1).get(0));
    }

    @Override
    public String getQuery() {
        return "SELECT * FROM Company";
    }

    public static class Result extends DataOut {
        public final String name;
        public final String city;

        public Result(int id, String name, String city) {
            super(id);
            this.name = name;
            this.city = city;
        }
    }
}
