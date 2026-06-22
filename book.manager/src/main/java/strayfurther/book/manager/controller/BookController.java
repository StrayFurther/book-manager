package strayfurther.book.manager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import strayfurther.book.manager.dto.BookPageDTO;
import strayfurther.book.manager.dto.CreateBookDTO;
import strayfurther.book.manager.dto.CreateReviewDTO;
import strayfurther.book.manager.dto.GetBookDTO;
import strayfurther.book.manager.dto.GetReviewDTO;
import strayfurther.book.manager.dto.UpdateBookDTO;
import strayfurther.book.manager.service.BookService;
import strayfurther.book.manager.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final ReviewService reviewService;

    @PostMapping("/")
    public ResponseEntity<GetBookDTO> createBook(@RequestBody @Valid CreateBookDTO dto) {
        return ResponseEntity.ok(bookService.create(dto));
    }

    @GetMapping("/")
    public ResponseEntity<BookPageDTO> getAllBooks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String titleContains,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ResponseEntity.ok(bookService.findAll(author, titleContains, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetBookDTO> getBookById(@PathVariable Long id) {
        return ResponseEntity.of(bookService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetBookDTO> updateBook(@PathVariable Long id, @RequestBody @Valid UpdateBookDTO dto) {
        return ResponseEntity.ok(bookService.updateBook(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.delete(id));
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<GetReviewDTO> addReviewToBook(@PathVariable Long id, @RequestBody @Valid CreateReviewDTO dto) {
        return ResponseEntity.ok(bookService.addReview(id, dto));
    }

    @GetMapping("/{id}/review")
    public ResponseEntity<List<GetReviewDTO>> getReviewsForBook(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.findByBookId(id));
    }
}
