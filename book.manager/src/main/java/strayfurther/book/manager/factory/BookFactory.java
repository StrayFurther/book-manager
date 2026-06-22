package strayfurther.book.manager.factory;

import strayfurther.book.manager.dto.CreateBookDTO;
import strayfurther.book.manager.exception.BookFactoryException;
import strayfurther.book.manager.model.Book;

public class BookFactory {
    public Book fromEntity(CreateBookDTO dto) {
        if (dto == null) {
            throw new BookFactoryException("Book DTO is null");
        }
        if (dto.getTitle() == null || dto.getAuthor() == null) {
            throw new BookFactoryException("Title and Author are required fields");
        }
        return Book.builder()
            .author(dto.getAuthor())
            .isbn(dto.getIsbn())
            .title(dto.getTitle())
            .description(dto.getDescription())
            .publicationYear(dto.getPublicationYear())
        .build();
    }
}
