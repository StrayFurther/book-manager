package strayfurther.book.manager.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import strayfurther.book.manager.dto.BookPageDTO;
import strayfurther.book.manager.dto.CreateBookDTO;
import strayfurther.book.manager.dto.CreateReviewDTO;
import strayfurther.book.manager.dto.GetBookDTO;
import strayfurther.book.manager.dto.GetReviewDTO;
import strayfurther.book.manager.dto.UpdateBookDTO;
import strayfurther.book.manager.exception.BookException;
import strayfurther.book.manager.exception.BookNotFoundException;
import strayfurther.book.manager.factory.BookFactory;
import strayfurther.book.manager.factory.GetBookDTOFactory;
import strayfurther.book.manager.factory.GetReviewDTOFactory;
import strayfurther.book.manager.model.Book;
import strayfurther.book.manager.model.Review;
import strayfurther.book.manager.repository.BookRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookFactory bookFactory = new BookFactory();
    private final GetBookDTOFactory bookDTOFactory = new GetBookDTOFactory();
    private final GetReviewDTOFactory reviewDTOFactory = new GetReviewDTOFactory();
    private final ReviewService reviewService;

    public GetBookDTO create(CreateBookDTO dto) {
        if (dto == null) {
            throw new BookException("Title and Author are required fields");
        }
        Book saved = bookRepository.save(bookFactory.fromEntity(dto));
        return bookDTOFactory.fromEntity(saved);
    }

    public GetBookDTO updateBook(Long id, UpdateBookDTO dto) {
        if (dto == null) {
            throw new BookException("Update payload is required");
        }
        if (id == null) {
            throw new BookException("Book id is required");
        }
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new BookNotFoundException("Book with id " + id + " does not exist")
        );
        if (dto.getTitle() != null) book.setTitle(dto.getTitle());
        if (dto.getAuthor() != null) book.setAuthor(dto.getAuthor());
        if (dto.getDescription() != null) book.setDescription(dto.getDescription());
        return bookDTOFactory.fromEntity(bookRepository.save(book));
    }

    public boolean delete(Long id) {
        if (id == null) {
            throw new BookException("Book id is required");
        }
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book with id " + id + " does not exist");
        }
        bookRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public Optional<GetBookDTO> findById(Long id) {
        return bookRepository.findById(id).map(book -> {
            List<GetReviewDTO> reviews = reviewService.findByBookId(book.getId());
            return bookDTOFactory.fromEntity(book, reviews);
        });
    }

    public BookPageDTO findAll(String author, String titleContains, Integer page, Integer size) {
        int safePage = page == null || page < 0 ? 0 : page;
        int safeSize = size == null || size < 1 ? 10 : Math.min(size, 100);
        Pageable pageable = PageRequest.of(safePage, safeSize);

        Specification<Book> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (author != null && !author.isBlank()) {
                predicate = cb.and(predicate, cb.equal(cb.lower(root.get("author")), author.toLowerCase()));
            }
            if (titleContains != null && !titleContains.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("title")), "%" + titleContains.toLowerCase() + "%"));
            }
            return predicate;
        };

        Page<Book> books = bookRepository.findAll(spec, pageable);
        return BookPageDTO.builder()
                .content(books.getContent().stream().map(bookDTOFactory::fromEntity).toList())
                .totalElements((int) books.getTotalElements())
                .totalPages(books.getTotalPages())
                .currentPage(books.getNumber())
                .build();
    }

    public GetReviewDTO addReview(Long bookId, CreateReviewDTO dto) {
        if (bookId == null || dto == null) {
            throw new BookException("Book id and review data are required");
        }
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new BookNotFoundException("Book with id " + bookId + " does not exist")
        );
        Review review = reviewService.create(dto, book);
        book.getReviews().add(review);
        bookRepository.save(book);
        return reviewDTOFactory.fromEntity(review);
    }
}
