import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class IOCTest {

    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeAll
    public static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void getInstance() {
        ITestedClass testedClass = IOC.getInstance(TestedClass.class,ITestedClass.class);

        double[] primitiveArray = {13.25D,2.195D};
        Double[] wrapArray = {5.2D,6.7D};
        String[] strArray = {"elem1","elem2"};

        Map<Integer,Double[]> map = new HashMap<>();
        map.put(1,wrapArray);
        map.put(2,wrapArray);

        ArrayList<String[]> list = new ArrayList<>();
        list.add(strArray);
        list.add(strArray);

        testedClass.logMap(map);
        Assertions.assertTrue(
                outContent.toString().contains("[1 => [5.2, 6.7],2 => [5.2, 6.7]]")
        );
        outContent.reset();

        testedClass.logPrimitiveArray(primitiveArray);
        Assertions.assertTrue(
                outContent.toString().contains("[13.25, 2.195]")
        );
        outContent.reset();

        testedClass.loggerMethod(map,list);
        Assertions.assertTrue(
                outContent.toString().contains("[1 => [5.2, 6.7],2 => [5.2, 6.7]], [[elem1, elem2], [elem1, elem2]]")
        );
    }

    @AfterAll
    public static void cleanUpStreams() {
        System.setOut(null);
    }
}