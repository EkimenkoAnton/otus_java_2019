package ru.otus;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import ru.otus.annotations.AfterAll;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.lang.reflect.Method;

class AnnotationProcessorTest {

    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeAll
    public static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @SneakyThrows
    @Test
    public void breakTest() {
        String methodName = "run";
        Class<?> checkingClass = FailedAnnotationsTest.class;
        boolean isAccessible = true;

        Class<?> clazz = TestRunner.class;
        Class<?>[] argType = {Class.class};
        Method method = clazz.getDeclaredMethod(methodName, argType);

        try {
            if (!method.canAccess(null))
                isAccessible = false;
            method.setAccessible(true);
            method.invoke(null, checkingClass);
        } finally {
            if (!isAccessible)
                method.setAccessible(false);
        }

        String outputStr = outContent.toString();
        Assertions.assertTrue(
                outputStr.contains("break") &&
                        outputStr.contains("AfterEach3") &&
                        outputStr.contains("AfterEach2") &&
                        outputStr.contains("AfterAll")
        );
    }

    @AfterAll
    public static void cleanUpStreams() {
        System.setOut(null);
    }

}