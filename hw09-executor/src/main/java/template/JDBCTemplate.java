package template;

import executor.Executor;
import executor.ExecutorImpl;
import lombok.SneakyThrows;
import tools.QueryGenerator;
import tools.ReflectionHelper;
import tools.SQLHelper;
import tools.TableMetaInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class JDBCTemplate<T> implements Template<T> {

    private Connection connection;
    private final TableMetaInfo tableMetaInfo;

    JDBCTemplate(Connection connection, Class<T> clazz) throws Exception {
        this.connection = connection;
        this.tableMetaInfo = new TableMetaInfo(clazz);
        if (tableMetaInfo.getKey() == null)
            throw new Exception("Unsupported сlass");
        init();
    }

    private void init() throws SQLException {
        String query = QueryGenerator.getInstance().generateCreate(
                tableMetaInfo.getName(), tableMetaInfo.getKey(), tableMetaInfo.getFieldTypes()
        );
        new ExecutorImpl(connection).update(query);
    }


    void updateConnection(Connection connection) {

        try {
            if (null != this.connection && !this.connection.isClosed())
                return;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.connection = connection;
    }

    @Override
    @SneakyThrows
    public void create(T objectData) {
        Map<String, Object> fields = getFields(objectData);
        Executor executor = new ExecutorImpl(connection);
        try {
            executor.update(
                    QueryGenerator.getInstance().generateInsert(tableMetaInfo.getName(),fields.keySet()),
                    fields.values().toArray()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    @SneakyThrows
    public void update(T objectData) {
        Map<String, Object> fields = getFields(objectData);
        List<Object> params = new ArrayList<>(fields.values());
        params.add(fields.get(tableMetaInfo.getKey()));
        Executor executor = new ExecutorImpl(connection);
        try {
            executor.update(
                    QueryGenerator.getInstance().generateUpdate(
                            tableMetaInfo.getName(), tableMetaInfo.getKey(), fields.keySet()
                    ),
                    params.toArray()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createOrUpdate(T objectData) { //TODO : implementation is not atomic


        Map<String, Object> fields = getFields(objectData);

        long id = (long) fields.get(tableMetaInfo.getKey());

        Object duplicate = load(id, objectData.getClass());

        if (null == duplicate)
            create(objectData);
        else
            update(objectData);
    }

    @Override
    public <I> I load(long id, Class<I> clazz) {

        Executor executor = new ExecutorImpl(connection);
        String query = QueryGenerator.getInstance().generateSelect(tableMetaInfo.getName(), tableMetaInfo.getKey());
        Optional<I> instance = Optional.empty();
        try {
            instance = executor.singleQuery(resultSet -> {
                        try {
                            int count = resultSet.getMetaData().getColumnCount();
                            Map<String, Object> recordMap = new HashMap<>(count);
                            for (int i = 1; i <= count; i++) {
                                String key = resultSet.getMetaData().getColumnName(i).toLowerCase();
                                Class<?> type = tableMetaInfo.getFieldTypes().get(key);
                                Object value = SQLHelper.extractParam(type, resultSet, i);
                                recordMap.put(key, value);
                            }
                            return ReflectionHelper.instantiate(clazz, recordMap);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }, query, id
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instance.orElse(null);
    }

    private Map<String,Object> getFields(Object object) {
        Map<String,Object> fields = new HashMap<>();
        for (String field : tableMetaInfo.getFieldNames()) {
            fields.put(field,ReflectionHelper.getFieldValue(object, field));
        }
        return fields;
    }
}
