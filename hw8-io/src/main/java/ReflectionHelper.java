
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

public final class ReflectionHelper {

    private static final Object[] NO_ARGS = {};
    private static final Object EMPTY = new Object();

    private ReflectionHelper() {
    }

    public static <T> T instantiate(Class<T> type, Object... args) {
        Constructor<T> constructor = null;
        boolean isAccessible = true;
        try {
            if (args.length == 0) {
                constructor = type.getDeclaredConstructor();
                isAccessible = constructor.canAccess(null);
                constructor.setAccessible(true);
                return constructor.newInstance();
            } else {
                final Class<?>[] classes = toClasses(args);
                constructor = type.getDeclaredConstructor(classes);
                isAccessible = constructor.canAccess(null);
                constructor.setAccessible(true);
                return constructor.newInstance(args);
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            if (!isAccessible)
                constructor.setAccessible(false);
        }

        return null;
    }

    public static <T> T instantiate(Class<T> type) {
        return instantiate(type,NO_ARGS);
    }

    public static Object getFieldValue(Object object, String name) {
        Field field = null;
        boolean isAccessible = true;
        try {
            field = object.getClass().getDeclaredField(name); //getField() for public fields
            isAccessible = field.canAccess(object);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if (field != null && !isAccessible) {
                field.setAccessible(false);
            }
        }
        return null;
    }

    public static void setFieldValue(Object object, String name, Object value) {
        Field field = null;
        boolean isAccessible = true;
        try {
            field = object.getClass().getDeclaredField(name); //getField() for public fields
            isAccessible = field.canAccess(object);
            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if (field != null && !isAccessible) {
                field.setAccessible(false);
            }
        }
    }

    public static Object callMethod(Object object, Method method, Object... args) {
        Object retVal;
        if (null == method)
            return null;
        boolean isAccessible = true;
        try {
            if (!method.canAccess(object))
                isAccessible = false;
            method.setAccessible(true);
            retVal = method.invoke(object, args);
            return retVal == null ? EMPTY : retVal;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            if (!isAccessible)
                method.setAccessible(false);
        }
        return null;
    }

    public static Object callMethod(Object object, String name, Object... args) {
        try {
            return callMethod(
                    object,
                    object.getClass().getDeclaredMethod(name, toClasses(args)),
                    args
            );
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object callMethod(Object object, Method method) {
        return callMethod(object,method,NO_ARGS);
    }

    public static Object callStaticMethod(Method method) {
        return callMethod(null,method);
    }

    public static<T> Method getMethodByName(T object, String name, Object[] args) {
        if (null == args) args = NO_ARGS;

        Class<?>[] argClasses = toClasses(args);
        Method[] declaredMethods = object.getClass().getDeclaredMethods();

        for (Method method : declaredMethods) {
            if(!method.getName().equals(name))
                continue;

            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 0 && (null == args || args.length == 0))
                return method;
            if (null == args)
                return null;
            if (args.length != parameterTypes.length)
                continue;
            for (int i = 0; i < parameterTypes.length; i++)
                if(!argClasses[i].isAssignableFrom(parameterTypes[i]))
                    break;
            return method;
        }

        return null;
    }

    public static Map<String,Object> getFields(Object object) {

        Map<String,Object> map = new HashMap<>();
        boolean isAccessible = true;
        Object localObject;

        for (Field field : object.getClass().getDeclaredFields()) {
            try {
                localObject = object;
                if(Modifier.isStatic(field.getModifiers()))
                    localObject = null;
                isAccessible = field.canAccess(localObject);
                field.setAccessible(true);
                map.put(field.getName(),field.get(localObject));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (field != null && !isAccessible)
                    field.setAccessible(false);
            }
        }
        return map;
    }

    private static Class<?>[] toClasses(Object[] args) {
        final Class<?>[] classes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++)
            classes[i] = args[i] != null ? args[i].getClass() : null;
        return classes;
    }
}