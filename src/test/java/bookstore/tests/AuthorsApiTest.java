package bookstore.tests;

import bookstore.models.Author;
import bookstore.utils.ApiClient;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class AuthorsApiTest {

    @Test
    void getAllAuthors_shouldReturnList() {
        ApiClient.getRequestSpec()
                .get("/Authors")
                .then()
                .statusCode(200)
                .body("$", not(empty()));
    }

    @Test
    void createAuthor_withInvalidBookId_shouldReturn400or404() {
        Map<String, Object> author = new HashMap<>();
        author.put("idBook", 999999); // Non-existent book
        author.put("firstName", "Ghost");
        author.put("lastName", "Writer");

        ApiClient.getRequestSpec()
                .body(author)
                .post("/Authors")
                .then()
                .statusCode(anyOf(is(400), is(404)));
    }

    @Test
    void updateAuthor_missingFirstName_shouldReturn400() {
        // Create author first
        Map<String, Object> author = new HashMap<>();
        author.put("idBook", 1);
        author.put("firstName", "Temp");
        author.put("lastName", "Author");

        Author created = ApiClient.getRequestSpec()
                .body(author)
                .post("/Authors")
                .then()
                .statusCode(200)
                .extract().as(Author.class);

        // Try to update with missing firstName
        Map<String, Object> update = new HashMap<>();
        update.put("id", created.id);
        update.put("idBook", created.idBook);
        update.put("lastName", "Author");

        ApiClient.getRequestSpec()
                .body(update)
                .put("/Authors/" + created.id)
                .then()
                .statusCode(anyOf(is(400), is(422)));

        // Clean up
        ApiClient.getRequestSpec()
                .delete("/Authors/" + created.id)
                .then()
                .statusCode(200);
    }

    @Test
    void getAuthor_invalidId_shouldReturn404() {
        ApiClient.getRequestSpec()
                .get("/Authors/999999")
                .then()
                .statusCode(404);
    }
}
