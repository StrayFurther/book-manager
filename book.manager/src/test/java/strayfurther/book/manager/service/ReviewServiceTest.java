package strayfurther.book.manager.service;

import org.hibernate.AssertionFailure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import strayfurther.book.manager.dto.CreateBookDTO;
import strayfurther.book.manager.dto.CreateReviewDTO;
import strayfurther.book.manager.dto.GetBookDTO;
import strayfurther.book.manager.dto.GetReviewDTO;
import strayfurther.book.manager.exception.ReviewException;
import strayfurther.book.manager.model.Book;
import strayfurther.book.manager.repository.BookRepository;
import strayfurther.book.manager.repository.ReviewRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ReviewServiceTest {
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private BookService bookService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    public void setUp() {
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
    }

    private CreateBookDTO createBookDto(String title, String author, String isbn, Integer publicationYear, String description) {
        CreateBookDTO dto = new CreateBookDTO();
        dto.setTitle(title);
        dto.setAuthor(author);
        dto.setIsbn(isbn);
        dto.setPublicationYear(publicationYear);
        dto.setDescription(description);
        return dto;
    }

    private CreateReviewDTO createReviewDto(String content, Integer rating) {
        CreateReviewDTO dto = new CreateReviewDTO();
        dto.setContent(content);
        dto.setRating(rating);
        return dto;
    }

    @Test
    public void createReview_success() {
        CreateBookDTO dto = createBookDto("Test Book", "Test Author", "1234567890", 2024, "This is a test book");

        GetBookDTO createdBook = bookService.create(dto);
        Book book = bookRepository.findById(createdBook.getId()).orElseThrow(
                () -> new AssertionFailure("Book not found")
        );

        CreateReviewDTO reviewDTO = createReviewDto("This is a test review", 4);

        reviewService.create(reviewDTO, book);
    }

    @Test
    public void createReview_failure_RatingMissing() {
        CreateBookDTO dto = createBookDto("Test Book", "Test Author", "1234567890", 2024, "This is a test book");

        GetBookDTO createdBook = bookService.create(dto);
        Book book = bookRepository.findById(createdBook.getId()).orElseThrow(
                () -> new AssertionFailure("Book not found")
        );

        CreateReviewDTO reviewDTO = createReviewDto("This is a test review", null);

        assertThrows(ReviewException.class,() -> reviewService.create(reviewDTO, book));
    }

    @Test
    public void createReview_failure_ContentMissing() {
        CreateBookDTO dto = createBookDto("Test Book", "Test Author", "1234567890", 2024, "This is a test book");

        GetBookDTO createdBook = bookService.create(dto);
        Book book = bookRepository.findById(createdBook.getId()).orElseThrow(
                () -> new AssertionFailure("Book not found")
        );

        CreateReviewDTO reviewDTO = createReviewDto(null, 4);

        assertThrows(ReviewException.class,() -> reviewService.create(reviewDTO, book));
    }

    @Test
    public void getReviewsByBookId_success() {
        CreateBookDTO dto = createBookDto("Test Book", "Test Author", "1234567890", 2024, "This is a test book");

        GetBookDTO createdBook = bookService.create(dto);

        CreateBookDTO dto2 = createBookDto("Test Book2", "Other Test Author", "1234567891", 2026, "This is another test book");

        GetBookDTO createdBook2 = bookService.create(dto2);
        Book book = bookRepository.findById(createdBook.getId()).orElseThrow(
                () -> new AssertionFailure("Book not found")
        );

        Book book2 = bookRepository.findById(createdBook2.getId()).orElseThrow(
                () -> new AssertionFailure("Book not found")
        );

        CreateReviewDTO reviewDTO = createReviewDto("This is a test review", 4);

        reviewService.create(reviewDTO, book);

        CreateReviewDTO reviewDTO2 = createReviewDto("This is a test review 2", 1);

        reviewService.create(reviewDTO2, book2);

        CreateReviewDTO reviewDTO3 = createReviewDto("This is a test review 3", 3);

        reviewService.create(reviewDTO3, book);

        List<GetReviewDTO> reviews = reviewService.findByBookId(createdBook.getId());
        assertEquals(2, reviews.size());
        assertEquals(reviewDTO.getContent(), reviews.getFirst().getContent());
        assertEquals(reviewDTO3.getContent(), reviews.getLast().getContent());
    }
}
