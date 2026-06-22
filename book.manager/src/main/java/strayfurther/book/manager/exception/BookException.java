package strayfurther.book.manager.exception;

public class BookException extends RuntimeException {
    public BookException(String message) {
        super(message);
    }

    public BookException(String message, Throwable cause) {
        super(message, cause);
    }
}