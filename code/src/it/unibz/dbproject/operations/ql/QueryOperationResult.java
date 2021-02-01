package it.unibz.dbproject.operations.ql;

import it.unibz.dbproject.exceptions.TypeCastException;
import it.unibz.dbproject.operations.OperationResult;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class QueryOperationResult<T> extends OperationResult<T> {
    private final List<T> resultObj;
    private final List<String> columnsWithVBar;

    public QueryOperationResult() {
        this.resultObj = new ArrayList<>();
        this.columnsWithVBar = new ArrayList<>();
    }

    @Override
    public void add(T result) {
        resultObj.add(result);
    }

    @Override
    public void addAll(Collection<T> results) {
        resultObj.addAll(results);
    }

    @Override
    public void clearValues() {
        resultObj.clear();
        columnsWithVBar.clear();
    }

    @Override
    public List<T> get() {
        return resultObj;
    }

    @Override
    public String getString() {
        return getTabular();
    }

    public String getTabular() {
        if (resultObj.size()==0)
            return "No result found for the query\n";

        if (columnsWithVBar.size() == 0)
            columnsWithVBar.addAll(interpolateWithVBar(getFieldsName()));
        List<String> result = new ArrayList<>();
        String formatString = buildFormatString();

        int lowestIdPos = 0;
        int i = 0;
        for (T row : resultObj) {
            List<String> rowValuesWithId = interpolateWithVBar(getFieldsValue(row));
            if (Integer.parseInt(rowValuesWithId.get(rowValuesWithId.size()-1)) == 1)
                lowestIdPos = Math.max(lowestIdPos, i);
            result.add(String.format(formatString, rowValuesWithId.toArray()));
            i++;
        }

        return buildHeader(formatString) + "\n" +
               result.subList(lowestIdPos, result.size()).parallelStream().collect(Collectors.joining("\n"));
    }

    private String buildFormatString() {
        StringBuilder formatBuilder = new StringBuilder();
        int[] maxColLength = new int[columnsWithVBar.size()]; //Add space for ID and vertical bars '|'

        //Loop on returned values and pick the longest value length (skip ID and vertical bars idx, not present in queryResult)
        for(T tuple : resultObj) {
            Collection<Object> actualValues = getFieldsValue(tuple);
            int i=0;
            for(Object obj: actualValues) {
                maxColLength[(i*2)] = Math.max(maxColLength[(i*2)], (obj==null) ? 2 : String.valueOf(obj).length());
                i++;
            }
        }

        //Loop on column headers and pick longest length
        int i=0;
        for(String key : columnsWithVBar) {
            maxColLength[i] = Math.max(maxColLength[i], key.length());
            formatBuilder.append("%-").append(maxColLength[i]+2).append("s");
            i++;
        }

        return formatBuilder.toString();
    }

    private String buildHeader(String formatString) {
        String header = String.format(formatString, columnsWithVBar.toArray());
        header += "\n" + "-".repeat(header.length());

        return header;
    }

    private List<String> interpolateWithVBar(List<?> row) {
        List<String> rowStr = new ArrayList<>();
        for(Object obj : row) {
            rowStr.add((obj==null) ? " " : obj.toString());
            rowStr.add("|");
        }
        rowStr.remove(rowStr.size()-1);

        return rowStr;
    }

    private List<Object> getFieldsName() {
        List<Object> cols = new ArrayList<>(0);

        if (resultObj.size() > 0)
            cols = Arrays.stream(resultObj.get(0).getClass().getFields())
                    .map(this::getFieldName)
                    .collect(Collectors.toList());

        return cols;
    }

    private String getFieldName(Field field) {
        String name = field.getName().replace('_', ' ');

        return Arrays.stream(name.split("\\s+"))
                .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1))
                .collect(Collectors.joining(" "));
    }

    private List<Object> getFieldsValue(T elem) {
        return Arrays.stream(elem.getClass().getFields())
            .map(r -> {
                try {
                    return r.get(elem);
                } catch (IllegalAccessException e) {
                    throw new TypeCastException("20030", "objects creation", "Field values are not compatible");
                }
            })
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "QueryResultOutput{" +
                "resultObj=" + resultObj.size() +
                '}';
    }
}


