package bookstore.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import java.io.IOException;
import java.util.Map;
import static org.hamcrest.Matchers.hasItem;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {
    public static final String BASE_URL = System.getProperty("base.url", "https://fakerestapi.azurewebsites.net/api/v1");

    @BeforeAll
    static void setupRestAssured() {
        RestAssured.baseURI = BASE_URL;
    }

    protected static Map<String, Object> loadBookFromJson(String resourcePath) throws IOException {
        return new ObjectMapper().readValue(
                BaseTest.class.getClassLoader().getResourceAsStream(resourcePath),
                new TypeReference<Map<String, Object>>() {
                });
    }

    // Common error validation helper
    public void assertInvalidIdError(ValidatableResponse response, String id) {
        response.statusCode(400)
                .body("errors.id", hasItem("The value '" + id + "' is not valid."));
    }
}
