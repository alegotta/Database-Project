package it.unibz.dbproject;

import it.unibz.dbproject.exceptions.DbException;
import it.unibz.dbproject.exceptions.MalformedParameterException;
import it.unibz.dbproject.operations.UserInput;

import java.sql.Date;
import java.sql.*;
import java.util.*;

public class Db {
    private static Db singleton;
    private final Connection connection;

    private Db(Connection connection) {
        this.connection = connection;
    }

    public static void connect(String dbms, String host, String port, String name, String username, String password) {
        Connection conn = null;
        DriverManager.setLoginTimeout(10);
        String url = "jdbc:" + dbms + "://" + host + ":" + port + "/" + name;

        try {
            conn = DriverManager.getConnection(url, username, password);
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            DbException.handleException(e, "connecting");
        }

        singleton = new Db(conn);
    }

    public static Db getInstance() {
        return singleton;
    }

    public static void disconnect() {
        try {
            if (singleton!=null && singleton.connection!=null && !singleton.connection.isClosed())
                singleton.connection.close();
        } catch (Exception e) {
            //Not relevant
        }
        singleton = null;
    }


    public int insert(String query, Collection<UserInput<?>> params) {
        PreparedStatement statement = prepareStatement(query, params);
        int affectedRows = 0;

        try {
            affectedRows = statement.executeUpdate();
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            DbException.handleException(e, query);
            try {
                connection.rollback();
            } catch (SQLException ex) {
                DbException.handleException(ex, query);
            }
        }
        return affectedRows;
    }

    public int insertBatch(String query, List<List<UserInput<?>>> rows) {
        int affectedRows = 0;

        try {
            PreparedStatement statement = connection.prepareStatement(query);

            for(List<UserInput<?>> params : rows) {
                bindParamToStatement(statement, params);
                statement.addBatch();
            }
            int[] ret = statement.executeBatch();
            affectedRows = Arrays.stream(ret).reduce(0, Integer::sum);

            statement.close();
            connection.commit();
        } catch (SQLException e) {
            DbException.handleException(e, query);
            try {
                connection.rollback();
            } catch (SQLException ex) {
                DbException.handleException(ex, query);
            }
        }
        return affectedRows;
    }

    public List<Map<String,Object>> select(String query, Collection<UserInput<?>> params) {
        PreparedStatement statement = prepareStatement(query, params);
        ResultSet resultSet;

        try {
            resultSet = statement.executeQuery();
            statement.close();
            return getResults(resultSet);
        } catch (SQLException e) {
            DbException.handleException(e, query);
        }
        return new ArrayList<>();
    }

    private PreparedStatement prepareStatement(String query, Collection<UserInput<?>> params) {
        long actualParamCount = query.chars().filter(ch -> ch == '?').count();

        if (actualParamCount != params.size())
            throw new MalformedParameterException("select", "Wrong number of parameters. Expected " + params.size() + ", actual " + actualParamCount);

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            bindParamToStatement(statement, params);
        } catch (SQLException e) {
            DbException.handleException(e, query);
        }

        return statement;
    }

    private void bindParamToStatement(PreparedStatement statement, Collection<UserInput<?>> params) {
        try {
            int i=1;
            for(UserInput<?> p: params) {
                Object param = p.getFirstValue();

                if (param==null || param=="null")
                    statement.setNull(i, p.getDataType());
                else {
                    switch (p.getDataType()) {
                        case Types.VARCHAR, Types.CHAR -> statement.setString(i, param.toString());
                        case Types.INTEGER -> statement.setInt(i, (Integer) param);
                        case Types.BOOLEAN, Types.BIT -> statement.setBoolean(i, (Boolean) param);
                        case Types.DATE -> statement.setDate(i, (Date) param);
                        case Types.TIME -> statement.setTime(i, (Time) param);
                        case Types.FLOAT, Types.DOUBLE -> statement.setDouble(i, (Double) param);
                        case Types.OTHER, Types.JAVA_OBJECT -> statement.setObject(i, param, Types.OTHER);
                        default -> throw new SQLException("Unknown type for input value " + param);
                    }
                }
                i++;
            }
        } catch (SQLException e) {
            DbException.handleException(e, "parameters casting");
        }
    }

    private List<Map<String,Object>> getResults(ResultSet resultSet) {
        List<Map<String,Object>> ret = new ArrayList<>();
        List<String> colNames = new ArrayList<>();
        List<Integer> colTypes = new ArrayList<>();

        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            for (int i=1; i<=numberOfColumns; i++) {
                colTypes.add(metaData.getColumnType(i));
                colNames.add(metaData.getColumnLabel(i));
            }

            while (resultSet.next()) {
                Map<String,Object> tuple = new HashMap<>(numberOfColumns);

                for (int i=1; i<=numberOfColumns; i++)
                    tuple.put(colNames.get(i-1), castParamFromResult(resultSet, i, colTypes));

                ret.add(tuple);
            }
        } catch (SQLException e) {
            DbException.handleException(e, "results conversion");
        }

        return ret;
    }

    private Object castParamFromResult(ResultSet resultSet, int pos, List<Integer> colTypes) {
        int colType = colTypes.get(pos-1);
        Object param = null;

        try {
            switch (colType) {
                case Types.CHAR, Types.VARCHAR, Types.OTHER -> param = resultSet.getString(pos);
                case Types.INTEGER -> param = resultSet.getInt(pos);
                case Types.BOOLEAN, Types.BIT -> param = resultSet.getBoolean(pos);
                case Types.DATE -> param = resultSet.getDate(pos);
                case Types.TIME -> param = resultSet.getTime(pos);
                case Types.FLOAT, Types.DOUBLE -> param = resultSet.getDouble(pos);
                case Types.NULL -> param = "null";
                default -> throw new SQLException("Unknown data type " + UserInput.getDataTypeName(colType) +  " in result for column " + pos, "22030");
            }
        } catch (SQLException e) {
            DbException.handleException(e, "type casting");
        }
        return param;
    }
}
