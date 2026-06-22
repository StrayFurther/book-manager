package strayfurther.book.manager.exception;

public class GetReviewFactoryDTOException extends RuntimeException {
    public GetReviewFactoryDTOException(String message) {
        super(message);
    }

    public GetReviewFactoryDTOException(String message, Throwable cause) {
        super(message, cause);
    }
}