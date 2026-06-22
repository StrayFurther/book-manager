package strayfurther.book.manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import strayfurther.book.manager.dto.CreateReviewDTO;
import strayfurther.book.manager.dto.GetReviewDTO;
import strayfurther.book.manager.exception.ReviewException;
import strayfurther.book.manager.factory.GetReviewDTOFactory;
import strayfurther.book.manager.factory.ReviewFactory;
import strayfurther.book.manager.model.Book;
import strayfurther.book.manager.model.Review;
import strayfurther.book.manager.repository.ReviewRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewFactory reviewFactory = new ReviewFactory();
    private final GetReviewDTOFactory reviewDTOFactory = new GetReviewDTOFactory();

    public Optional<Review> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return reviewRepository.findById(id);
    }

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Review create(CreateReviewDTO dto, Book book) {
        if (dto == null) {
            throw new ReviewException("Review DTO is null");
        }
        if (book == null) {
            throw new ReviewException("Book is null");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new ReviewException("Review content is required");
        }
        if (dto.getRating() == null) {
            throw new ReviewException("Review rating is required");
        }
        return reviewRepository.save(reviewFactory.fromEntities(dto, book));
    }

    @Transactional(readOnly = true)
    public List<GetReviewDTO> findByBookId(Long bookId) {
        if (bookId == null) {
            throw new ReviewException("Book ID is null");
        }
        return reviewRepository.findByBook_Id(bookId)
                .stream()
                .map(reviewDTOFactory::fromEntity)
                .toList();
    }
}
