package ru.otus;

public class TestRunner {
    public static void main(String[] args) {
        run(AnnotationsTest.class);
    }

    private static void run(Class<?> testClass) {
        // JUnit 5
        //final LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
        //        .selectors(
        //                selectClass(testClass)
        //        )
        //        .build();

        //final Launcher launcher = LauncherFactory.create();
        //launcher.execute(request);

//        JUnit 4
//        JUnitCore junit = new JUnitCore();
//        junit.run(testClass.class);

        AnnotationProcessor processor = new AnnotationProcessor();
        processor.run(testClass);
    }
}
