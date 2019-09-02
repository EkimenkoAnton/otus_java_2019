package tools;

import annatations.Id;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public final class TableMetaInfo {
    @Getter private final String name;
    @Getter private final String key;
    private final Map<String, Class<?>> fields = new HashMap<>();

    @SneakyThrows
    public  TableMetaInfo(Class clazz) {
        this.name = clazz.getSimpleName();
        String localKey = null;
        for (Field field : clazz.getDeclaredFields()) {
            fields.put(field.getName(),field.getType());
            if(field.isAnnotationPresent(Id.class)) {
                if (null == localKey)
                    localKey = field.getName();
            }
        }
        this.key = localKey;
    }

    public Set<String> getFieldNames(){
        return fields.keySet();
    }

    public Map<String, Class<?>> getFieldTypes(){
        return fields;
    }

}
