package tools;

import lombok.SneakyThrows;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SQLHelper {

    private static final Map<Class<?>, String> javaToSqlTypeNameMapping = new HashMap<>();
    
    static {
        javaToSqlTypeNameMapping.put(int.class,"INTEGER");
        javaToSqlTypeNameMapping.put(Integer.class,"INTEGER");
        javaToSqlTypeNameMapping.put(long.class,"LONG");
        javaToSqlTypeNameMapping.put(Long.class,"LONG");
        javaToSqlTypeNameMapping.put(boolean.class,"BOOLEAN");
        javaToSqlTypeNameMapping.put(Boolean.class,"BOOLEAN");
        javaToSqlTypeNameMapping.put(double.class,"DOUBLE");
        javaToSqlTypeNameMapping.put(Double.class,"DOUBLE");
        javaToSqlTypeNameMapping.put(float.class,"FLOAT");
        javaToSqlTypeNameMapping.put(Float.class,"FLOAT");
        javaToSqlTypeNameMapping.put(String.class,"VARCHAR");
        javaToSqlTypeNameMapping.put(char.class,"VARCHAR");
        javaToSqlTypeNameMapping.put(java.sql.Date.class,"DATE");
        javaToSqlTypeNameMapping.put(Timestamp.class,"TIMESTAMP");
        javaToSqlTypeNameMapping.put(java.util.Date.class,"TIMESTAMP");
        javaToSqlTypeNameMapping.put(java.sql.Time.class,"TIME");
        javaToSqlTypeNameMapping.put(Object.class,"BLOB");
    }
    

    @SneakyThrows
    public static void setParams(PreparedStatement statement, Object... params) {
        if(params == null)
            return;
        for (int i=0;i<params.length;i++) {
            setParam(statement, i+1, params[i]);
        }
    }

    @SneakyThrows
    public static void setParam(PreparedStatement statement, int index, Object param) {
        if (param == null)
            statement.setNull(index,Types.OTHER);
        /*
        else if (param instanceof Byte)
            statement.setInt(index, ((Byte) param).intValue());
        else if (param instanceof String)
            statement.setString(index, (String) param);
        else if (param instanceof BigDecimal)
            statement.setBigDecimal(index, (BigDecimal) param);
        else if (param instanceof BigInteger)
            statement.setString(index, param.toString());
        else if (param instanceof Short)
            statement.setShort(index, (Short) param);
        else if (param instanceof Integer)
            statement.setInt(index, (Integer) param);
        else if (param instanceof Long)
            statement.setLong(index, (Long) param);
        else if (param instanceof Float)
            statement.setFloat(index, (Float) param);
        else if (param instanceof Double)
            statement.setDouble(index, (Double) param);
        else if (param instanceof byte[])
            statement.setBytes(index, (byte[]) param);
        else if (param instanceof Date)
            statement.setDate(index, (Date) param);
        else if (param instanceof Time)
            statement.setTime(index, (Time) param);
        else if (param instanceof Timestamp)
            statement.setTimestamp(index, (Timestamp) param);
        else if (param instanceof Boolean)
            statement.setBoolean(index, (Boolean) param);
        else if (param instanceof InputStream)
            statement.setBinaryStream(index, (InputStream) param, -1);
        else if (param instanceof Blob)
            statement.setBlob(index, (Blob) param);
        else if (param instanceof Clob)
            statement.setClob(index, (Clob) param);
        else if (param instanceof java.util.Date)
            statement.setTimestamp(index, new Timestamp(((java.util.Date) param).getTime()));
        else*/
            statement.setObject(index, param);
    }

    @SneakyThrows
    public static Object extractParam(Class<?> tClass, ResultSet rs, int id) {
        Object retVal;
        int typeId = rs.getMetaData().getColumnType(id);

        if(typeId==java.sql.Types.ARRAY)
            retVal = rs.getArray(id);
        else if (typeId==java.sql.Types.BIGINT)
            retVal = rs.getObject(id);
        else if(typeId==java.sql.Types.INTEGER || typeId==java.sql.Types.TINYINT || typeId==java.sql.Types.SMALLINT)
            retVal = rs.getInt(id);//retVal = rs.getInt(id);
        else if(typeId==java.sql.Types.BOOLEAN || typeId==java.sql.Types.BIT)
            retVal = rs.getBoolean(id);
        else if(typeId==java.sql.Types.BLOB)
            retVal = rs.getBlob(id);
        else if(typeId== Types.CLOB)
            retVal = rs.getClob(id);
        else if(typeId==java.sql.Types.DOUBLE)
            retVal = rs.getDouble(id);
        else if (typeId == Types.DECIMAL || typeId == Types.NUMERIC)
            retVal = rs.getBigDecimal(id);
        else if(typeId==java.sql.Types.FLOAT || typeId== Types.REAL)
            retVal = rs.getFloat(id);
        else if(typeId==java.sql.Types.NVARCHAR || typeId == Types.LONGNVARCHAR || typeId == Types.NCHAR)
            retVal = rs.getNString(id);
        else if(typeId==java.sql.Types.VARCHAR || typeId == Types.CHAR || typeId == Types.LONGVARCHAR)
            retVal = rs.getString(id);
        else if(typeId==java.sql.Types.DATE)
            retVal = rs.getDate(id);
        else if(typeId==java.sql.Types.TIMESTAMP)
            retVal = rs.getTimestamp(id);
        else if(typeId==java.sql.Types.TIME)
            retVal = rs.getTime(id);
        else if(typeId==java.sql.Types.BINARY || typeId==java.sql.Types.VARBINARY || typeId==java.sql.Types.LONGVARBINARY)
            retVal = rs.getBytes(id);
        else
            retVal = rs.getObject(id);
        return retVal;
    }

    public static String getSqlTypeName(Class<?> clazz){
        String type = javaToSqlTypeNameMapping.get(clazz);
        return null != type ? type : "TEXT";
    }
}
