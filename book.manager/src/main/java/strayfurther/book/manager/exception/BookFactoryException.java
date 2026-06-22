package strayfurther.book.manager.exception;

public class BookFactoryException extends RuntimeException {
    public BookFactoryException(String message) {
        super(message);
    }

    public BookFactoryException(String message, Throwable cause) {
        super(message, cause);
    }
}