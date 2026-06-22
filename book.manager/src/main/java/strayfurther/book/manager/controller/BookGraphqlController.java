package strayfurther.book.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import strayfurther.book.manager.dto.AddReviewDTO;
import strayfurther.book.manager.dto.BookPageDTO;
import strayfurther.book.manager.dto.CreateBookDTO;
import strayfurther.book.manager.dto.CreateReviewDTO;
import strayfurther.book.manager.dto.GetBookDTO;
import strayfurther.book.manager.dto.GetReviewDTO;
import strayfurther.book.manager.dto.UpdateBookDTO;
import strayfurther.book.manager.service.BookService;
import strayfurther.book.manager.service.ReviewService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookGraphqlController {
    private final BookService bookService;
    private final ReviewService reviewService;

    // ── Queries ──────────────────────────────────────────────────────────────

    @QueryMapping
    public BookPageDTO allBooks(@Argument String author,
                                @Argument String titleContains,
                                @Argument Integer page,
                                @Argument Integer size) {
        return bookService.findAll(author, titleContains, page, size);
    }

    @QueryMapping
    public GetBookDTO bookById(@Argument Long id) {
        return bookService.findById(id).orElse(null);
    }

    @QueryMapping
    public List<GetReviewDTO> reviewsByBookId(@Argument Long bookId) {
        return reviewService.findByBookId(bookId);
    }

    /**
     * Resolves the `reviews` field on the GraphQL `Book` type.
     * Spring GraphQL only calls this when the client actually requests the field,
     * avoiding unnecessary DB queries for list/page results.
     * For bookById the reviews are already loaded in the DTO; returning them here
     * avoids a second round-trip.
     */
    @SchemaMapping(typeName = "Book", field = "reviews")
    public List<GetReviewDTO> getReviewsForBook(GetBookDTO book) {
        return reviewService.findByBookId(book.getId());
    }

    // ── Mutations ─────────────────────────────────────────────────────────────

    @MutationMapping
    public GetBookDTO createBook(@Argument CreateBookDTO input) {
        return bookService.create(input);
    }

    @MutationMapping
    public GetBookDTO updateBook(@Argument Long id, @Argument UpdateBookDTO input) {
        return bookService.updateBook(id, input);
    }

    @MutationMapping
    public Boolean deleteBook(@Argument Long id) {
        return bookService.delete(id);
    }

    @MutationMapping
    public GetReviewDTO addReview(@Argument AddReviewDTO input) {
        CreateReviewDTO reviewDTO = new CreateReviewDTO();
        reviewDTO.setContent(input.getContent());
        reviewDTO.setRating(input.getRating());
        return bookService.addReview(input.getBookId(), reviewDTO);
    }
}
