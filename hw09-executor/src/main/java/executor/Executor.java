package executor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface Executor {
    int update(String sql, Object... params) throws SQLException;
    <T> List<T> query(Function<ResultSet, T> handler, String sql, Object... params) throws SQLException;
    <T> Optional<T> singleQuery(Function<ResultSet, T> handler, String sql, Object... params) throws SQLException;
}
