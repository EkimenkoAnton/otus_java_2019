import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class JsonConverter {

    public String toJson(Object object) {
        if(null == object)
            return "";
        StringBuilder sb = new StringBuilder();
        Map<String, Object> fields = ReflectionHelper.getFields(object);
        appendObject(sb, fields);
        return sb.toString();
    }

    private <K> void appendObject(StringBuilder sb, Map<K, Object> values){
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

    private void appendValue(StringBuilder sb, Object arg) {
        if (((Object) arg).getClass().isArray())
            appendArray(sb, toArray(arg));
        else if (arg instanceof Collection)
            appendArray(sb, ((Collection) arg).toArray());
        else if (arg instanceof Map)
            appendObject(sb,(Map<Object, Object>) arg);
        else {
            if (arg instanceof  Number)
                sb.append(arg);
            else
                sb.append("\"").append(arg).append("\"");
        }
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
