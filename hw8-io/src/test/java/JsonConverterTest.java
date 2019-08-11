import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;

class JsonConverterTest {

    private TestedClass testedClass;
    private JsonConverter jsonConverter;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        testedClass = new TestedClass();
        jsonConverter = new JsonConverter();
    }

    @org.junit.jupiter.api.Test
    void toJson() {
        Gson gson = new Gson();
        String testedJson = jsonConverter.toJson(testedClass);
        String googleJson = gson.toJson(testedClass);

        TestedClass testedExtractedClass = gson.fromJson(testedJson, TestedClass.class);
        TestedClass googleExtractedClass = gson.fromJson(googleJson, TestedClass.class);

        Assertions.assertTrue(googleExtractedClass.equals(testedExtractedClass));
    }
}