package executor;

import tools.SQLHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ExecutorImpl implements Executor {

    private final Connection connection;

    public ExecutorImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int update(String sql, Object... params) throws SQLException {

        int generedKey = 0;
        connection.setAutoCommit(false);
        Savepoint savepoint = connection.setSavepoint("update");
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            SQLHelper.setParams(statement,params);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()){
                if(null != resultSet && resultSet.next())
                    generedKey = (int) resultSet.getLong(1);
            }
            connection.commit();
            return generedKey;
        } catch (SQLException e) {
            connection.rollback(savepoint);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public int update(String sql) throws SQLException {
        return update(sql, null);
    }

    @Override
    public <T> List<T> query(Function<ResultSet, T> handler, String sql, Object... params) throws SQLException {
        List<T> retValList = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            SQLHelper.setParams(statement,params);
            try (ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()) {
                    retValList.add(handler.apply(resultSet));
                }
                return  retValList;
            }
        }
    }

    public <T> List<T> query(Function<ResultSet, T> handler, String sql) throws SQLException {
        return query(handler,sql,null);
    }

    @Override
    public <T> Optional<T> singleQuery(Function<ResultSet, T> handler, String sql, Object... params) throws SQLException {
        List<T> list = query(handler,sql,params);
        return list.isEmpty() ? Optional.empty() : Optional.ofNullable(list.get(0));
    }

    public <T> Optional<T> singleQuery(Function<ResultSet, T> handler, String sql) throws SQLException {
        return singleQuery(handler,sql,null);
    }

}
