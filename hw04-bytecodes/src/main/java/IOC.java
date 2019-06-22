import annotations.Log;
import util.ReflectionHelper;
import util.StringConverter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;


public class IOC {

    private IOC() {}

    @SuppressWarnings("unchecked")
    private static Object createProxyInstance(Class clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();

        if (interfaces == null || interfaces.length == 0)
            throw new IllegalArgumentException("class does not implement interfaces");

        InvocationHandler handler = new DemoInvocationHandler<>(
                ReflectionHelper.instantiate(clazz)
        );
        return Proxy.newProxyInstance(
                clazz.getClassLoader(),
                interfaces,
                handler
        );
    }

    public static<I> I getInstance(Class clazz, Class<I> iClazz) {
        return iClazz.cast(createProxyInstance(clazz));
    }

    private static class DemoInvocationHandler<S> implements InvocationHandler {
        private final S myClass;
        private final Map<Method,Boolean> loggedMethodsCache = new HashMap<>();

        DemoInvocationHandler(S myClass) {
            this.myClass = myClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (isNeedLog(method,args)) {
                System.out.println(
                        "invoking method "+method.getName() +
                                ", incoming params : "+ StringConverter.argsToString(args)
                );
            }
            return method.invoke(myClass, args);
        }

        private Boolean isNeedLog(Method method, Object[] args) {
            Boolean retVal;
            if((retVal = loggedMethodsCache.get(method)) == null) {

                Method classMethod = ReflectionHelper.getMethodByName(myClass, method.getName(), args);
                retVal = method.isAnnotationPresent(Log.class) || (classMethod != null && classMethod.isAnnotationPresent(Log.class));
                loggedMethodsCache.put(method,retVal);
            }
            return retVal;
        }

    }
}
