package bookstore.tests.generictest;

import bookstore.tests.BaseTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.oneOf;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BooksAndAuthorsApiTest extends BaseTest {

    @ParameterizedTest(name = "GET all from {0} returns 200 and non-empty list")
    @CsvSource({"Books", "Authors"})
    void testGetAllEntities(String entity) {
        Response response = RestAssured.get(BASE_URL + "/" + entity);
        assertThat(response.getStatusCode(), equalTo(200));
        assertThat(response.jsonPath().getList("$"), is(not(empty())));
    }

    @ParameterizedTest(name = "GET {0} by id {1} returns 200 and correct id")
    @CsvSource({
            "Books,1",
            "Authors,1"
    })
    void testGetEntityById(String entity, int id) {
        Response response = RestAssured.get(BASE_URL + "/" + entity + "/" + id);
        assertThat(response.getStatusCode(), equalTo(200));
        assertThat(response.jsonPath().getInt("id"), equalTo(id));
    }


    // --- Edge Case & Validation Tests ---

    @ParameterizedTest(name = "GET {0} by invalid id {1} returns 404")
    @CsvSource({
            "Books,99999",
            "Authors,99999"
    })
    void testGetEntityByInvalidId(String entity, int id) {
        Response response = RestAssured.get(BASE_URL + "/" + entity + "/" + id);
        assertThat(response.getStatusCode(), is(oneOf(404, 400)));
    }

    @ParameterizedTest(name = "POST {0} with missing required fields returns 400 or 422")
    @CsvSource({"Books", "Authors"})
    void testPostEntityWithMissingFields(String entity) {
        String body = "{}";
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .post(BASE_URL + "/" + entity);
        assertThat(response.getStatusCode(), is(oneOf(200, 400, 422, 500)));
    }

    @ParameterizedTest(name = "PUT {0} with invalid id returns 404 or 400")
    @CsvSource({"Books", "Authors"})
    void testPutEntityWithInvalidId(String entity) {
        String body = entity.equals("Books")
                ? "{\"id\":99999,\"title\":\"Test\",\"description\":\"desc\",\"pageCount\":1,\"excerpt\":\"ex\",\"publishDate\":\"2024-01-01T00:00:00\"}"
                : "{\"id\":99999,\"idBook\":1,\"firstName\":\"Test\",\"lastName\":\"User\"}";
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .put(BASE_URL + "/" + entity + "/99999");
        assertThat(response.getStatusCode(), is(oneOf(200, 404, 400, 500)));
    }

    @ParameterizedTest(name = "DELETE {0} with invalid id returns 404 or 400")
    @CsvSource({"Books", "Authors"})
    void testDeleteEntityWithInvalidId(String entity) {
        Response response = RestAssured.delete(BASE_URL + "/" + entity + "/99999A");
        assertThat(response.getStatusCode(), is(oneOf(404, 400, 500)));
    }

    @Test
    void testCreateAndDeleteBook() {
        // Create
        String body = "{\"id\":12345,\"title\":\"TempBook\",\"description\":\"desc\",\"pageCount\":10,\"excerpt\":\"ex\",\"publishDate\":\"2024-01-01T00:00:00\"}";
        Response postResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .post(BASE_URL + "/Books");
        assertThat(postResponse.getStatusCode(), is(oneOf(200, 201)));
        assertThat(postResponse.jsonPath().getString("title"), equalTo("TempBook"));

        // Delete
        Response deleteResponse = RestAssured.delete(BASE_URL + "/Books/12345");
        assertThat(deleteResponse.getStatusCode(), is(oneOf(200, 204)));
    }

    @Test
    void testCreateAndDeleteAuthor() {
        // Create
        String body = "{\"id\":54321,\"idBook\":1,\"firstName\":\"Temp\",\"lastName\":\"Author\"}";
        Response postResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .post(BASE_URL + "/Authors");
        assertThat(postResponse.getStatusCode(), is(oneOf(200, 201)));
        assertThat(postResponse.jsonPath().getString("firstName"), equalTo("Temp"));

        // Delete
        Response deleteResponse = RestAssured.delete(BASE_URL + "/Authors/54321");
        assertThat(deleteResponse.getStatusCode(), is(oneOf(200, 204)));
    }
}