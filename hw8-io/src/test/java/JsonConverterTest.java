import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

class JsonConverterTest {

    private TestedClass testedClass;
    private JsonConverter jsonConverter;

    @BeforeEach
    void setUp() {
        testedClass = new TestedClass();
        jsonConverter = new JsonConverter();
    }

    @Test
    void toJson() {
        Gson gson = new Gson();
        String testedJson = jsonConverter.toJson(testedClass);
        String googleJson = gson.toJson(testedClass);

        TestedClass testedExtractedClass = gson.fromJson(testedJson, TestedClass.class);
        TestedClass googleExtractedClass = gson.fromJson(googleJson, TestedClass.class);

        Assertions.assertEquals(googleExtractedClass, testedExtractedClass);
    }

    @Test
    void customTest() {
        Gson gson = new Gson();
        Assertions.assertEquals(gson.toJson(null), jsonConverter.toJson(null));
        Assertions.assertEquals(gson.toJson((byte)1), jsonConverter.toJson((byte)1));
        Assertions.assertEquals(gson.toJson((short)1f), jsonConverter.toJson((short)1f));
        Assertions.assertEquals(gson.toJson(1), jsonConverter.toJson(1));
        Assertions.assertEquals(gson.toJson(1L), jsonConverter.toJson(1L));
        Assertions.assertEquals(gson.toJson(1f), jsonConverter.toJson(1f));
        Assertions.assertEquals(gson.toJson(1d), jsonConverter.toJson(1d));
        Assertions.assertEquals(gson.toJson("aaa"), jsonConverter.toJson("aaa"));
        Assertions.assertEquals(gson.toJson('a'), jsonConverter.toJson('a'));
        Assertions.assertEquals(gson.toJson(new int[] {1, 2, 3}), jsonConverter.toJson(new int[] {1, 2, 3}));
        Assertions.assertEquals(gson.toJson(List.of(1, 2 ,3)), jsonConverter.toJson(List.of(1, 2 ,3)));
        Assertions.assertEquals(gson.toJson(Collections.singletonList(1)), jsonConverter.toJson(Collections.singletonList(1)));
    }
}