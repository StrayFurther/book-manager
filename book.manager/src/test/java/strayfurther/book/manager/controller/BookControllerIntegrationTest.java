package strayfurther.book.manager.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import strayfurther.book.manager.repository.BookRepository;
import strayfurther.book.manager.repository.ReviewRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerIntegrationTest {

    @Autowired
    private Environment environment;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    void createBook_andSearchBooks_work() throws Exception {
        String payload = """
                {
                  "title": "Interview Book",
                  "author": "ALTEN",
                  "isbn": "1234567890",
                  "publicationYear": 2024,
                  "description": "A demo book"
                }
                """;

        HttpResponse<String> createResponse = sendJsonPost("/api/book/", payload);
        assertEquals(200, createResponse.statusCode(), createResponse.body());
        assertTrue(createResponse.body().contains("Interview Book"), createResponse.body());

        HttpResponse<String> searchResponse = sendGet("/api/book/?author=ALTEN&page=0&size=10");
        assertEquals(200, searchResponse.statusCode(), searchResponse.body());
        assertTrue(searchResponse.body().contains("\"totalElements\":1"), searchResponse.body());
        assertTrue(searchResponse.body().contains("Interview Book"), searchResponse.body());
    }

    @Test
    void createBook_validationError_returnsStructuredResponse() throws Exception {
        HttpResponse<String> response = sendJsonPost("/api/book/", "{}");
        assertEquals(400, response.statusCode(), response.body());
        assertTrue(response.body().contains("\"status\":400"), response.body());
        assertTrue(response.body().contains("Title is required"), response.body());
    }

    private HttpResponse<String> sendJsonPost(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString())
                ;
    }

    private HttpResponse<String> sendGet(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + path))
                .GET()
                .build();
        return HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString())
                ;
    }

    private String baseUrl() {
        return "http://localhost:" + environment.getProperty("local.server.port");
    }
}







