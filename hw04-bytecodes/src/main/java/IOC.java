import annotations.Log;
import util.ReflectionHelper;
import util.StringConverter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class IOC {

    private IOC() {}

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

        DemoInvocationHandler(S myClass) {
            this.myClass = myClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            Method classMethod = ReflectionHelper.getMethodByName(myClass, method.getName(), args);

            if (method.isAnnotationPresent(Log.class)
                    || (classMethod != null && classMethod.isAnnotationPresent(Log.class))) {
                System.out.println(
                        "invoking method "+method.getName() +
                        ", incoming params : "+ StringConverter.argsToString(args)
                );
            }

            return method.invoke(myClass, args);
        }

    }
}
