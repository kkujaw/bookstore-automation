package bookstore.utils;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class ApiClient {

    public static RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .header("Content-Type", "application/json");
    }
}