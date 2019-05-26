package ru.otus;

import ru.otus.annotations.*;

public class FailedAnnotationsTest extends AnnotationsTest {
    @BeforeAll
    static void beforeAll() {
        System.out.println("BeforeAll");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("AfterAll");
    }

    FailedAnnotationsTest() {
        System.out.println("Call of the constructor");
    }

    @BeforeEach
    void beforeEach3() {
        System.out.println("BeforeEach3");
        throw new NullPointerException();
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("BeforeEach");
    }

    @BeforeEach
    void beforeEach2() {
        System.out.println("BeforeEach2");
    }

    @Test
    void testOne() {
        System.out.println("testOne");
    }

    @Test
    void testTwo() {
        System.out.println("testTwo");
    }

    @AfterEach
    void afterEach3() {
        System.out.println("AfterEach3");
    }

    @AfterEach
    void afterEach2() {
        System.out.println("AfterEach2");
    }
}
