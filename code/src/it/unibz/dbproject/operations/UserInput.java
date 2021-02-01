package it.unibz.dbproject.operations;

import it.unibz.dbproject.exceptions.MalformedParameterException;
import it.unibz.dbproject.exceptions.TypeCastException;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class UserInput<T> {
    private final List<T> values;

    private final int dataType;
    private String typeDesc;

    private final String inputDesc;

    private final boolean userVisible;

    public UserInput(int dataType, String inputDesc) {
        this(dataType, inputDesc, "", true);
    }

    public UserInput(int dataType, String inputDesc, String typeDesc, boolean userVisible) {
        this.values = new ArrayList<>();
        this.dataType = dataType;
        this.typeDesc = typeDesc;
        this.inputDesc = inputDesc;
        this.userVisible = userVisible;
    }

    public void addValue(Object newValue) {
        values.add(tryConvert(newValue));
    }
    public void addAllValues(Collection<Object> newValues) {
        for(Object newValue : newValues)
            values.add(tryConvert(newValue));
    }
    public void setFirstValue(Object newValue) {
        if (values.size() >= 1)
            values.set(0, tryConvert(newValue));
        else
            addValue(newValue);
    }
    protected void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }
    protected void clearValues() {
        values.clear();
    }

    private T tryConvert(Object value) {
        Object ret;
        String strValue = value.toString();

        if (strValue.equalsIgnoreCase("null"))
            return null;

        try {
            switch(dataType) {
                case Types.VARCHAR, Types.CHAR, Types.OTHER, Types.JAVA_OBJECT -> ret = strValue;
                case Types.FLOAT, Types.DOUBLE -> ret = Double.parseDouble(strValue);
                case Types.INTEGER -> ret = Integer.parseInt(strValue);
                case Types.BOOLEAN -> ret = Boolean.parseBoolean(strValue);
                case Types.DATE -> ret = Date.valueOf(strValue);
                case Types.TIME -> ret = Time.valueOf(strValue+":00");
                default -> throw new TypeCastException("22030", "Input Casting", "Unknown type parameter " + dataType + " for input " + strValue);
            }
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new MalformedParameterException("20030", "type input", "Wrong input: " + strValue + ". Expected type: " + getDataTypeName(dataType), e);
        }
        return (T) ret;
    }

    public static String getDataTypeName(int dataType) {
        return Arrays.stream(Types.class.getFields())
                .filter(field -> {
                    try {
                        return ((Integer) field.get(-1)) == dataType;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(Field::getName)
                .findFirst()
                .orElse(String.valueOf(dataType));
    }

    public List<T> getValues() {
        return values;
    }

    public Object getFirstValue() {
        if (values.size()>0)
            return values.get(0);
        else
            return new Object();
    }

    public int getDataType() {
        return dataType;
    }

    public String getTypeDesc() {
        if (typeDesc.isBlank())
            return typeDesc;
        else
            return " ["+typeDesc+"]";
    }

    public String getInputDesc() {
        return inputDesc;
    }

    public boolean isUserVisible() {
        return userVisible;
    }
}
