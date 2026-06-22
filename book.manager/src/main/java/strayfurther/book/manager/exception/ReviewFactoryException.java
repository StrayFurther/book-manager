package strayfurther.book.manager.exception;

public class ReviewFactoryException extends RuntimeException {
    public ReviewFactoryException(String message) {
        super(message);
    }

    public ReviewFactoryException(String message, Throwable cause) {
        super(message, cause);
    }
}