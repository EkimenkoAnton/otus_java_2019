package tools;

import lombok.SneakyThrows;
import java.util.*;

public class QueryGenerator {

    private enum RequestType {
        CREATE, SELECT, UPDATE, INSERT
    }

    private static QueryGenerator instance;
    private final Map<RequestType,Map<String,String>> cache = new HashMap<>();

    private QueryGenerator() {
        cache.put(RequestType.CREATE, new HashMap<>());
        cache.put(RequestType.SELECT, new HashMap<>());
        cache.put(RequestType.UPDATE, new HashMap<>());
        cache.put(RequestType.INSERT, new HashMap<>());
    }

    public static QueryGenerator getInstance() {
        if (null == instance)
            instance = new QueryGenerator();
        return instance;
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

        query = new StringBuilder()
                .append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append("(")
                .append(fieldsBuilder)
                .append(");")
                .toString();
        putToCache(tableName,RequestType.CREATE,query);
        return query;
    }

    public String generateInsert(String tableName, Set<String> fields){

        String query;
        if ((query = getFromCache(tableName,RequestType.INSERT)) != null )
            return query;

        if (null == fields) return "";

        StringBuilder insertBuilder = new StringBuilder();
        StringBuilder nameBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();

        boolean appendCommon = false;
        for (String field : fields) {
            if (appendCommon) {
                nameBuilder.append(",");
                valueBuilder.append(",");
            }
            nameBuilder.append(field).append(" ");
            valueBuilder.append("?");
            appendCommon = true;
        }

        insertBuilder.append("INSERT INTO ").append(tableName).append(" ")
                .append("(").append(nameBuilder).append(") ")
                .append("VALUES")
                .append(" (").append(valueBuilder).append(")")
                .append(";");

        query = insertBuilder.toString();
        putToCache(tableName,RequestType.INSERT,query);

        return query;
    }

    @SneakyThrows
    public String generateUpdate(String tableName, String tableKey, Set<String> fields) {

        String query;

        if ((query = getFromCache(tableName,RequestType.UPDATE)) != null )
            return query;

        if (null == fields || fields.size() == 0) return "";

        StringBuilder updateBuilder = new StringBuilder();
        updateBuilder.append("UPDATE ").append(tableName).append(" set ");

        boolean appendCommon = false;
        for (String field : fields) {
            if (appendCommon)
                updateBuilder.append(",");
            updateBuilder.append(field).append("=").append("?");
            appendCommon = true;
        }

        query = updateBuilder
                .append(" WHERE ")
                .append(tableKey).append("=").append("?")
                .append(";").toString();
        putToCache(tableName,RequestType.UPDATE,query);

        return query;
    }

    public String generateSelect(String tableName, String tableKey) {
        String query;

        if ((query = getFromCache(tableName,RequestType.SELECT)) != null )
            return query;


        if (null == tableKey)
            return "";

        query = new StringBuilder()
                .append("SELECT * FROM ").append(tableName).append(" ")
                .append("WHERE ")
                .append(tableKey).append("=").append("?")
                .append(";").toString();
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
