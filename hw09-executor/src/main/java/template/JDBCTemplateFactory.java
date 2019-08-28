package template;

import annatations.Id;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class JDBCTemplateFactory {

    private static JDBCTemplateFactory instance;

    private final Map<Class, JDBCTemplate> templates = new HashMap<>();

    private JDBCTemplateFactory(){};

    public static JDBCTemplateFactory getInstance(){
        if(null == instance)
            instance = new JDBCTemplateFactory();
        return instance;
    }

    public <T> Template<T> getTemplateInstance(Connection connection, Class<T> tClass) throws Exception {
        JDBCTemplate template = templates.get(tClass);
        if(template == null) {
            for (Field field : tClass.getDeclaredFields()) {
                if(field.isAnnotationPresent(Id.class)) {
                    template = new JDBCTemplate<>(connection,tClass);
                    templates.put(tClass,template);
                    break;
                }
            }
        }
        if (template != null)
            template.updateConnection(connection);
        //TODO : not security cast, but i couldn't found another good way for this case
        return template;
    }
}
