package strayfurther.book.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import strayfurther.book.manager.model.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findByBook_Id(Long bookId);
    List<Review> findByBook_IdIn(List<Long> bookIds);
}
