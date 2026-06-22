package strayfurther.book.manager.factory;

import strayfurther.book.manager.dto.CreateReviewDTO;
import strayfurther.book.manager.exception.ReviewFactoryException;
import strayfurther.book.manager.model.Book;
import strayfurther.book.manager.model.Review;

import java.time.Instant;

public class ReviewFactory {

    public Review fromEntities(CreateReviewDTO dto, Book book) {
        if (dto == null) {
            throw new ReviewFactoryException("Review DTO is null");
        }
        if (book == null) {
            throw new ReviewFactoryException("Book is null");
        }
        return Review.builder()
                .book(book)
                .content(dto.getContent())
                .rating(dto.getRating())
                .date(Instant.now())
                .build();
    }
}
