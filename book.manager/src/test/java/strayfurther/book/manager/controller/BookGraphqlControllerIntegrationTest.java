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
class BookGraphqlControllerIntegrationTest {

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
    void createBook_addReview_andQueryBook_work() {
        HttpResponse<String> createResponse = postGraphQl("""
                mutation {
                  createBook(input: {
                    title: "GraphQL Interview Book"
                    author: "ALTEN"
                    isbn: "1234567890"
                    publicationYear: 2024
                    description: "GraphQL demo"
                  }) {
                    id
                    title
                    author
                  }
                }
                """);
        assertEquals(200, createResponse.statusCode(), createResponse.body());
        assertTrue(createResponse.body().contains("GraphQL Interview Book"), createResponse.body());

        String bookId = extractId(createResponse.body());

        HttpResponse<String> reviewResponse = postGraphQl(String.format("""
                mutation {
                  addReview(input: {
                    bookId: \"%s\"
                    content: \"Great book\"
                    rating: 5
                  }) {
                    content
                    date
                    rating
                    bookId
                  }
                }
                """, bookId));
        assertEquals(200, reviewResponse.statusCode(), reviewResponse.body());
        assertTrue(reviewResponse.body().contains("Great book"), reviewResponse.body());
        assertTrue(reviewResponse.body().contains("date"), reviewResponse.body());

        HttpResponse<String> queryResponse = postGraphQl(String.format("""
                query {
                  bookById(id: \"%s\") {
                    title
                    author
                  }
                  reviewsByBookId(bookId: \"%s\") {
                    content
                    date
                    rating
                  }
                }
                """, bookId, bookId));

        assertEquals(200, queryResponse.statusCode(), queryResponse.body());
        assertTrue(queryResponse.body().contains("GraphQL Interview Book"), queryResponse.body());
        assertTrue(queryResponse.body().contains("Great book"), queryResponse.body());
        assertTrue(queryResponse.body().contains("date"), queryResponse.body());
    }

    private HttpResponse<String> postGraphQl(String query) {
        String payload = """
                {"query": %s}
                """.formatted(toJsonString(query));
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl() + "/api/graphql"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            return HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString())
                    ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String extractId(String json) {
        int idIndex = json.indexOf("\"id\":");
        int start = json.indexOf('"', idIndex + 5) + 1;
        int end = json.indexOf('"', start);
        return json.substring(start, end);
    }

    private String toJsonString(String value) {
        return '"' + value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + '"';
    }

    private String baseUrl() {
        return "http://localhost:" + environment.getProperty("local.server.port");
    }
}

