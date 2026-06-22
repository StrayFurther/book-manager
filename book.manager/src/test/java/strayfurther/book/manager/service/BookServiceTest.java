package strayfurther.book.manager.service;

import org.hibernate.AssertionFailure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import strayfurther.book.manager.dto.CreateBookDTO;
import strayfurther.book.manager.dto.GetBookDTO;
import strayfurther.book.manager.dto.UpdateBookDTO;
import strayfurther.book.manager.exception.BookFactoryException;
import strayfurther.book.manager.model.Book;
import strayfurther.book.manager.repository.BookRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    public void setUp() {
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

    @Test
    public void createBook_success() {
        CreateBookDTO dto = createBookDto("Test Book", "Test Author", "1234567890", 2024, "This is a test book");

        GetBookDTO createdBook = bookService.create(dto);
        Book book = bookRepository.findById(createdBook.getId()).orElseThrow(
                () -> new RuntimeException("Book not found")
        );
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("1234567890", book.getIsbn());
        assertEquals(2024, book.getPublicationYear());
        assertEquals("This is a test book", book.getDescription());
    }

    @Test
    public void createBook_failure_NoTitle() {
        CreateBookDTO dto = createBookDto(null, "Test Author", "1234567890", 2024, "This is a test book");
        assertThrows(BookFactoryException.class, () -> bookService.create(dto));
    }

    @Test
    public void createBook_failure_NoAuthor() {
        CreateBookDTO dto = createBookDto("Test Book", null, "1234567890", 2024, "This is a test book");
        assertThrows(BookFactoryException.class, () -> bookService.create(dto));
    }

    @Test
    public void createBook_success_noDescriptionNoYearNoPublicationYear() {
        CreateBookDTO dto = createBookDto("Test Book", "Test Author", null, null, null);
        GetBookDTO createdBook = bookService.create(dto);
        Book book = bookRepository.findById(createdBook.getId()).orElseThrow(
                () -> new AssertionFailure("Book not found")
        );
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertNull(book.getIsbn());
        assertNull(book.getPublicationYear());
        assertNull(book.getDescription());
    }

    @Test
    public void getBookById_success() {
        CreateBookDTO dto = createBookDto("Test Book", "Test Author", null, null, null);
        GetBookDTO createdBook = bookService.create(dto);

        GetBookDTO book = bookService.findById(createdBook.getId())
                .orElseThrow(() -> new AssertionFailure("Book not found"));

        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
    }

    @Test
    public void getBookById_failure_NotFound() {
        Optional<GetBookDTO> bookOpt = bookService.findById(-1L);
        assertTrue(bookOpt.isEmpty());
    }

    @Test
    public void updateBook_success() {
        CreateBookDTO dto = createBookDto("Test Book", "Test Author", null, null, null);
        GetBookDTO createdBook = bookService.create(dto);

        UpdateBookDTO dto2 = new UpdateBookDTO();
        dto2.setTitle("Updated Book");
        dto2.setAuthor("Updated Author");
        dto2.setDescription("This is an updated test book");
        bookService.updateBook(createdBook.getId(), dto2);
        Book book = bookRepository.findById(createdBook.getId()).orElseThrow(
                () -> new AssertionFailure("Book not found")
        );
        assertEquals("Updated Book", book.getTitle());
        assertEquals("Updated Author", book.getAuthor());
        assertEquals("This is an updated test book", book.getDescription());
    }

    @Test
    public void updateBook_success_partialUpdate() {
        CreateBookDTO dto = createBookDto("Test Book", "Test Author", null, null, null);
        GetBookDTO createdBook = bookService.create(dto);

        UpdateBookDTO dto2 = new UpdateBookDTO();
        dto2.setTitle("Updated Book");
        bookService.updateBook(createdBook.getId(), dto2);
        Book book = bookRepository.findById(createdBook.getId()).orElseThrow(
                () -> new AssertionFailure("Book not found")
        );
        assertEquals("Updated Book", book.getTitle());
        assertNotEquals("Updated Author", book.getAuthor());
        assertNotEquals("This is an updated test book", book.getDescription());
    }
}
