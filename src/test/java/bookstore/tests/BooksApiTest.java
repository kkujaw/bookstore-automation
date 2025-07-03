package bookstore.tests;

import bookstore.models.Book;
import bookstore.utils.ApiClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class BooksApiTest {

    @Test
    void getAllBooks_shouldReturnList() {
        Response response = ApiClient.getRequestSpec()
                .get("/Books")
                .then()
                .statusCode(200)
                .extract().response();

        Book[] books = response.as(Book[].class);
        assertThat(books.length, greaterThan(0));
    }

    @Test
    void createAndDeleteBook_shouldSucceed() {
        Book newBook = new Book();
        newBook.title = "Test Book";
        newBook.description = "Test Description";
        newBook.pageCount = 123;
        newBook.excerpt = "Test Excerpt";
        newBook.publishDate = "2025-07-03T09:43:00Z";

        // Create
        Response createResp = ApiClient.getRequestSpec()
                .body(newBook)
                .post("/Books")
                .then()
                .statusCode(200)
                .extract().response();

        Book created = createResp.as(Book.class);
        assertThat(created.title, equalTo(newBook.title));

        // Delete
        ApiClient.getRequestSpec()
                .delete("/Books/" + created.id)
                .then()
                .statusCode(200);
    }

    @Test
    void getBook_invalidId_shouldReturn404() {
        ApiClient.getRequestSpec()
                .get("/Books/999999")
                .then()
                .statusCode(404);
    }

    @Test
    void getBooks_shouldReturnJsonContentType() {
        ApiClient.getRequestSpec()
                .get("/Books")
                .then()
                .statusCode(200)
                .contentType("application/json");
    }

    @Test
    void createBook_missingRequiredField_shouldReturn400() {
        Map<String, Object> incompleteBook = new HashMap<>();
        incompleteBook.put("description", "No title provided");
        incompleteBook.put("pageCount", 100);

        ApiClient.getRequestSpec()
                .body(incompleteBook)
                .post("/Books")
                .then()
                .statusCode(anyOf(is(400), is(422))); // Adjust based on API behavior
    }

    @Test
    void updateBook_invalidId_shouldReturn404() {
        Book updatedBook = new Book();
        updatedBook.id = 1100011; // Non-existent ID
        updatedBook.title = "Non-existent";
        updatedBook.description = "Should not update";
        updatedBook.pageCount = 1;
        updatedBook.excerpt = "No";
        updatedBook.publishDate = "2025-07-03T09:43:00Z";

        ApiClient.getRequestSpec()
                .body(updatedBook)
                .put("/Books/1100011")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteBook_twice_shouldReturn404SecondTime() {
        // Create a book
        Book book = new Book();
        book.title = "To be deleted";
        book.description = "Delete me twice";
        book.pageCount = 1;
        book.excerpt = "No";
        book.publishDate = "2025-07-03T09:43:00Z";

        Book created = ApiClient.getRequestSpec()
                .body(book)
                .post("/Books")
                .then()
                .statusCode(200)
                .extract().as(Book.class);

        // First delete
        ApiClient.getRequestSpec()
                .delete("/Books/" + created.id)
                .then()
                .statusCode(200);

        // Second delete should fail
        ApiClient.getRequestSpec()
                .delete("/Books/" + created.id)
                .then()
                .statusCode(404);
    }

}
