package tools;

import lombok.SneakyThrows;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QueryGenerator {

    private enum RequestType {
        CREATE, SELECT, UPDATE, INSERT
    }

    private final Map<RequestType,Map<String,String>> cache = new HashMap<>(4);

    public QueryGenerator() {
        cache.put(RequestType.CREATE, new HashMap<>());
        cache.put(RequestType.SELECT, new HashMap<>());
        cache.put(RequestType.UPDATE, new HashMap<>());
        cache.put(RequestType.INSERT, new HashMap<>());
    }

    public String generateCreate(String tableName, String tableKey, Map<String, Class<?>> fields) {
        String query;

        if ((query = getFromCache(tableName,RequestType.CREATE)) != null )
            return query;
        StringBuilder fieldsBuilder = new StringBuilder();
        if(null != tableKey) {
            fieldsBuilder.append(tableKey).append(" ").append(SQLHelper.getSqlTypeName(fields.get(tableKey))).append(" auto_increment");
        }
        for (String fieldName : fields.keySet()) {
            if(fieldName.equalsIgnoreCase(tableKey))
                continue;
            if (fieldsBuilder.length() > 0)
                fieldsBuilder.append(",");
            fieldsBuilder.append(fieldName).append(" ").append(SQLHelper.getSqlTypeName(fields.get(fieldName)));
        }

        query = "CREATE TABLE IF NOT EXISTS " +
                tableName +
                "(" +
                fieldsBuilder +
                ");";
        putToCache(tableName,RequestType.CREATE,query);
        return query;
    }

    public String generateInsert(String tableName, Set<String> fields){

        String query;
        if ((query = getFromCache(tableName,RequestType.INSERT)) != null )
            return query;

        if (null == fields) return "";

        StringBuilder sb = new StringBuilder();

        String fieldNames = String.join(", ", fields);
        //String fieldNames = fields.stream().collect(Collectors.joining(", "));
        String fieldValues = IntStream.range(0, fields.size()).mapToObj(i -> "?").collect(Collectors.joining(", "));

        sb.append("INSERT INTO ").append(tableName).append(" ")
                .append("(").append(fieldNames).append(") ")
                .append("VALUES")
                .append(" (").append(fieldValues).append(")")
                .append(";");

        query = sb.toString();
        putToCache(tableName,RequestType.INSERT,query);

        return query;
    }

    @SneakyThrows
    public String generateUpdate(String tableName, String tableKey, Set<String> fields) {

        String query;

        if ((query = getFromCache(tableName,RequestType.UPDATE)) != null )
            return query;

        if (null == fields || fields.size() == 0) return "";

        String settingValues= fields.stream().map(field -> field + "=" + "?").collect(Collectors.joining(", " ));

        query = "UPDATE " + tableName +
                " set " + settingValues +
                " WHERE " +
                tableKey + "=" + "?" +
                ";";
        putToCache(tableName,RequestType.UPDATE,query);

        return query;
    }

    public String generateSelect(String tableName, String tableKey) {
        String query;

        if ((query = getFromCache(tableName,RequestType.SELECT)) != null )
            return query;

        if (null == tableKey)
            return "";

        query = "SELECT * FROM " + tableName + " " +
                "WHERE " +
                tableKey + "=" + "?" +
                ";";
        putToCache(tableName,RequestType.SELECT,query);

        return query;
    }

    private void putToCache(String tableName, RequestType type, String query){
        cache.get(type).put(tableName,query);
    }

    private String getFromCache(String tableName, RequestType type){
        return cache.get(type).get(tableName);
    }

}
