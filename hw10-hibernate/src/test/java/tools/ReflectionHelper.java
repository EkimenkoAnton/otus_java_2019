package tools;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ReflectionHelper {

    private static final Object[] NO_ARGS = {};
    private static final Object EMPTY = new Object();

    private ReflectionHelper() {
    }

    public static <T> T instantiate(Class<T> type, Object... args) {
        Constructor<T> constructor;
        try {
            if (args.length == 0) {
                try {
                    constructor = type.getDeclaredConstructor();
                } catch (NoSuchMethodException ex) {
                    constructor = type.getConstructor();
                }
            } else {
                final Class<?>[] classes = toClasses(args);
                try {
                    constructor = type.getDeclaredConstructor(classes);
                } catch (NoSuchMethodException ex) {
                    constructor = type.getConstructor(classes);
                }
            }
        } catch (NoSuchMethodException e) {
            return null;
        }
        return instantiate(constructor,args);
    }

    public static <T> T instantiate(Constructor<T> constructor, Object... args) {
        if (null == constructor) return null;
        boolean isAccessible = true;
        try {
            isAccessible = constructor.canAccess(null);
            constructor.setAccessible(true);
            if (null == args || args.length == 0)
                return constructor.newInstance();
            else
                return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            if (!isAccessible)
                constructor.setAccessible(false);
        }
        return null;
    }
    public static <T> T instantiate(Class<T> type, Map<String,Object> args) {
        T instance = instantiateWithDefaultFields(type);
        setFieldValues(instance,args);
        return instance;
    }

    public static <T> T instantiateWithDefaultFields(Class<T> type) {
        if (null == type) return null;
        List<Object> args = new ArrayList<>();
        Constructor<T> constructor = null;
        Constructor<?> candidateConstructor = null;
        Constructor<?>[] declaredConstructors = type.getDeclaredConstructors();
        if (declaredConstructors == null || declaredConstructors.length == 0)
            declaredConstructors = type.getConstructors();
        for (Constructor<?> declaredConstructor : declaredConstructors) {
            if (null == candidateConstructor || declaredConstructor.getParameterCount() < candidateConstructor.getParameterCount())
                candidateConstructor = declaredConstructor;
        }
        if (null == candidateConstructor)
            return null;
        Class<?>[] parameterTypes = candidateConstructor.getParameterTypes();
        for (Class<?> parameterType : candidateConstructor.getParameterTypes()) {
            args.add(getDefaultValue(parameterType));
        }
        try {
            constructor = type.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            try {
                constructor = type.getConstructor(parameterTypes);
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            }
        }
        return instantiate(constructor,args.toArray());
    }

    private static <T> Object getDefaultValue(Class<T> type) {
        if (type.isPrimitive()) {
            if(type == int.class || type == long.class || type == byte.class)
                return 0;
            if(type == double.class || type == float.class)
                return 0.0;
            if(type == boolean.class)
                return false;
            if(type == char.class)
                return 32;
        }
        return null;
    }

    public static <T> T instantiate(Class<T> type) {
        return instantiate(type,NO_ARGS);
    }


    public static Object getFieldValue(Object object, String name) {
        try {
            return getFieldValue(object, object.getClass().getDeclaredField(name));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getFieldValue(Object object, Field field) {
        boolean isAccessible = true;
        try {
            isAccessible = field.canAccess(object);
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if (field != null && !isAccessible) {
                field.setAccessible(false);
            }
        }
        return null;
    }


    private static<T> Field getField(Class<T> type, String name) {
        try {
            return type.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            for (Field declaredField : type.getDeclaredFields())
                if (declaredField.getName().equalsIgnoreCase(name))
                    return declaredField;
        }
        return null;
    }

    private static Field getField(Object object, String name) {
        return getField(object.getClass(), name);
    }

    public static void setFieldValues(Object object, Map<String,Object> args) {
        if (null == object || null == args) return;
        args.keySet().forEach(fieldName -> setFieldValue(object, fieldName, args.get(fieldName)));
    }

    public static void setFieldValue(Object object, String name, Object value) {
        Field field = null;
        boolean isAccessible = true;
        try {
            field = getField(object,name);
            if (null == field)
                throw new NoSuchFieldException();
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