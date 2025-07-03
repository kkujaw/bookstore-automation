package bookstore.utils;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class ApiClient {
    private static final String BASE_URL = "https://fakerestapi.azurewebsites.net/api/v1";

    public static RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json");
    }
}