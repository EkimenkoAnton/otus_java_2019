import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class JsonConverter {

    public String toJson(Object object) {
        StringBuilder sb = new StringBuilder();
        appendValue(sb, object);
        return sb.toString();
    }

    private void appendObject(StringBuilder sb, Object object) {
        appendMap(sb, ReflectionHelper.getFields(object));
    }
    private <K> void appendMap(StringBuilder sb, Map<K, Object> values){
        int cnt = 0;
        sb.append("{");
        if (null != values) {
            for (K key : values.keySet()) {
                Object value = values.get(key);
                if (null == value) continue;
                if (cnt > 0) sb.append(",");
                appendEntity(sb, key, value);
                cnt++;
            }
        }
        sb.append("}");
    }

    private <K> void appendEntity(StringBuilder sb, K key, Object value) {
        sb.append("\"").append(key).append("\"")
                .append(":");
        appendValue(sb,value);
    }

    private void appendArray(StringBuilder sb, Object... values) {
        int cnt = 0;
        sb.append("[");
        for (Object value : values) {
            if (cnt>0) sb.append(",");
            appendValue(sb,value);
            cnt++;
        }
        sb.append("]");
    }

    private void appendValue(StringBuilder sb, Object value) {
        if(null == value)
            sb.append("null");
        else if (((Object) value).getClass().isArray())
            appendArray(sb, toArray(value));
        else if (value instanceof Collection)
            appendArray(sb, ((Collection) value).toArray());
        else if (value instanceof Map)
            appendMap(sb,(Map<Object, Object>) value);
        else if (value instanceof  Number)
            sb.append(value);
        else if (value instanceof String || value instanceof Character)
            sb.append("\"").append(value).append("\"");
        else
            appendObject(sb,value);

    }

    private Object[] toArray(Object val){
        if (val instanceof Object[])
            return (Object[])val;
        int arrayLength = Array.getLength(val);
        Object[] outputArray = new Object[arrayLength];
        for(int i = 0; i < arrayLength; ++i){
            outputArray[i] = Array.get(val, i);
        }
        return outputArray;
    }
}