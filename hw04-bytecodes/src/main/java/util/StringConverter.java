package util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class StringConverter {

    public static String argsToString(Object[] args) {
        if (null == args || args.length==0)
            return "";
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            if(sb.length()>0)
                sb.append(", ");
            if (null == arg)
                sb.append("null");
            else if (((Object) arg).getClass().isArray()) {
                sb.append("[");
                sb.append(argsToString(getArray(arg)));
                sb.append("]");
            } else if (arg instanceof Collection) {
                sb.append("[");
                sb.append(argsToString(((Collection) arg).toArray()));
                sb.append("]");
            } else if (arg instanceof Map) {
                sb.append("[");
                int cnt =0;
                for (Object key : ((Map) arg).keySet()) {
                    if (cnt>0) sb.append(",");
                    sb.append(argsToString(new Object[]{key}));
                    sb.append(" => ");
                    sb.append(argsToString(new Object[]{((Map) arg).get(key)}));
                    cnt++;
                }
                sb.append("]");
            } else
                sb.append(arg);
        }
        return sb.toString();
    }

    private static Object[] getArray(Object val){
        if (val instanceof Object[])
            return (Object[])val;
        int arrlength = Array.getLength(val);
        Object[] outputArray = new Object[arrlength];
        for(int i = 0; i < arrlength; ++i){
            outputArray[i] = Array.get(val, i);
        }
        return outputArray;
    }
}
