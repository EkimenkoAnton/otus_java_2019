package dbservice;

import cache.CacheEngine;
import cache.CacheEngineImpl;
import tools.ReflectionHelper;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CacheDBService implements DBService{

    private final Map<Class,CacheEngine<Long, Object>> cacheMap = new HashMap<>();

    private final Map<Class,Method> classToIdMethod = new HashMap<>();

    private final DBService dbService;

    public CacheDBService(DBService dbService) {
        this.dbService = dbService;
    }

    @Override
    public void create(Object objectData) {
        dbService.create(objectData);
        storeToCache(objectData);
    }

    @Override
    public void update(Object objectData) {
        dbService.update(objectData);
        storeToCache(objectData);
    }

    @Override
    public void createOrUpdate(Object objectData) {
        dbService.createOrUpdate(objectData);
        storeToCache(objectData);
    }

    @Override
    public <I> I load(long id, Class<I> clazz) {
        Object value;
        CacheEngine<Long, Object> cacheEngine = getCacheEngine(clazz);

        if (cacheEngine != null && null!= (value = cacheEngine.get(id))) {
            return clazz.cast(value);
        }

        value = dbService.load(id, clazz);

        if (cacheEngine != null && null!= value)
            cacheEngine.put(id, value);

        return clazz.cast(value);
    }

    private Method getGetIdMethod(Class<?> clazz){
        Method getIdMethod = classToIdMethod.get(clazz);

        if (null == getIdMethod) {
            getIdMethod = ReflectionHelper.getMethodByName(clazz, "getId");

            if (null == getIdMethod || getIdMethod.getReturnType() != long.class)
                return null;
            classToIdMethod.put(clazz, getIdMethod);
        }
        return getIdMethod;
    }

    private CacheEngine<Long,Object> getCacheEngine(Class clazz) {
        CacheEngine<Long, Object> cacheEngine = cacheMap.get(clazz);
        if (null == cacheEngine && isCachable(clazz)) {
            cacheEngine = new CacheEngineImpl<>(10,600,0,false);
            cacheMap.put(clazz,cacheEngine);
        }
        return cacheEngine;
    }

    private boolean isCachable(Class<?> clazz){
        Field field = ReflectionHelper.getField(clazz,"id");
        if(null == field) return false;
        return field.isAnnotationPresent(Id.class);
    }

    private void storeToCache(Object objectData){
        Long key;
        Method getIdMethod = getGetIdMethod(objectData.getClass());
        if (null != getIdMethod) {
            CacheEngine<Long, Object> cacheEngine = getCacheEngine(objectData.getClass());
            key = (Long) ReflectionHelper.callMethod(objectData, getIdMethod);
             cacheEngine.put(key,objectData);
        }
    }

}
