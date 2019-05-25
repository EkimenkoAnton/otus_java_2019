package ru.otus;

import lombok.SneakyThrows;
import ru.otus.annotations.*;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static ru.otus.reflection.ReflectionHelper.*;


public class AnnotationProcessor {

    private final Set<Method> beforeEachMethods = new HashSet<>();
    private final Set<Method> afterEachMethods = new HashSet<>();
    private final Set<Method> testMethods = new HashSet<>();

    private Method beforeAllMethod = null;
    private Method afterAllMethod = null;

    public void run(Class<?> testedClass) {
        if ( null == testedClass) return;

        init(testedClass);

        try {
            if (null == callStaticMethod(beforeAllMethod)) return;
            for (Method testMethod : testMethods) {
                Object instance = instantiate(testedClass);
                try {
                    if (!callBeforeEachMethods(instance)) break;
                    callTest(instance, testMethod);
                } finally {
                    callAfterEachMethods(instance);
                }
            }
        }finally {
            callStaticMethod(afterAllMethod);
        }
    }

    @SneakyThrows
    private void init(Class<?> testedClass) {
        Method[] methods = testedClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeAll.class)) {
                if (beforeAllMethod != null)
                    throw new Exception("override the BeforeAll method");
                beforeAllMethod = method;
            }
            if (method.isAnnotationPresent(AfterAll.class)) {
                if (afterAllMethod != null)
                    throw new Exception("override the AfterAll method");
                afterAllMethod = method;
            }
            if (method.isAnnotationPresent(BeforeEach.class))
                beforeEachMethods.add(method);
            if (method.isAnnotationPresent(AfterEach.class))
                afterEachMethods.add(method);
            if (method.isAnnotationPresent(Test.class))
                testMethods.add(method);
        }
    }

    private boolean callTest(Object object, Method method) {
        try {
            callMethod(object, method);
            return true;
        } catch (Throwable th) {
            System.out.println(
                    "failed to execute test " + (null == method ? "" : method.getName()) +
                            " error : " + th
            );
        }
        return false;
    }

    private boolean callBeforeEachMethods(Object object) {
        return callMethods(object,beforeEachMethods, false);
    }

    private boolean callAfterEachMethods(Object object) {
        return callMethods(object,afterEachMethods, true);
    }

    private boolean callMethods(Object object, Collection<Method> methods, Boolean ignoreExceptionMode) {
        for (Method method : methods) {
            try {
                if (null == callMethod(object, method) && !ignoreExceptionMode)
                    return false;
            } catch (Throwable th) {
                System.out.println(
                        "failed to execute method " + (null == method ? "" : method.getName()) +
                                " error : "+ th
                );
                if (!ignoreExceptionMode)
                    return false;
            }
        }
        return true;
    }

}
