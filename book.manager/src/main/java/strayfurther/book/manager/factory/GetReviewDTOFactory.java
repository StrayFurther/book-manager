package strayfurther.book.manager.factory;

import lombok.NoArgsConstructor;
import strayfurther.book.manager.dto.GetReviewDTO;
import strayfurther.book.manager.exception.GetReviewFactoryDTOException;
import strayfurther.book.manager.model.Review;

@NoArgsConstructor
public class GetReviewDTOFactory {
    public GetReviewDTO fromEntity(Review review) {
        if (review == null) {
            throw new GetReviewFactoryDTOException("Review is null");
        }
        return GetReviewDTO.builder()
                .id(review.getId())
                .content(review.getContent())
                .date(review.getDate())
                .rating(review.getRating())
                .bookId(review.getBook() != null ? review.getBook().getId() : null)
            .build();
    }
}
