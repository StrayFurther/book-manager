package strayfurther.book.manager.factory;

import lombok.NoArgsConstructor;
import strayfurther.book.manager.dto.GetBookDTO;
import strayfurther.book.manager.dto.GetReviewDTO;
import strayfurther.book.manager.exception.BookFactoryException;
import strayfurther.book.manager.model.Book;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class GetBookDTOFactory {
    public GetBookDTO fromEntity(Book book) {
        if (book == null) {
            throw new BookFactoryException("Book is null");
        }
        return fromEntity(book, new ArrayList<>());
    }

    public GetBookDTO fromEntity(Book book, List<GetReviewDTO> reviews) {
        if (book == null) {
            throw new BookFactoryException("Book is null");
        }
        return GetBookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .description(book.getDescription())
                .reviews(reviews != null ? reviews : new ArrayList<>())
            .build();
    }
}
