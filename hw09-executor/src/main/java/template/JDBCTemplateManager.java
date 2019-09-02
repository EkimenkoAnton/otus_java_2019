package template;

import annatations.Id;
import tools.QueryGenerator;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public final class JDBCTemplateManager {

    private final Map<Class, JDBCTemplate> templates = new HashMap<>();
    private final QueryGenerator generator = new QueryGenerator();

    public <T> Template<T> getTemplateInstance(Connection connection, Class<T> tClass) throws Exception {
        JDBCTemplate template = templates.get(tClass);
        if(template == null) {
            for (Field field : tClass.getDeclaredFields()) {
                if(field.isAnnotationPresent(Id.class)) {
                    template = new JDBCTemplate<>(connection,tClass,generator);
                    templates.put(tClass,template);
                    break;
                }
            }
        }
        if (template != null)
            template.updateConnection(connection);
        //TODO : not safe cast, but i couldn't found another good way for this case
        return template;
    }
}
