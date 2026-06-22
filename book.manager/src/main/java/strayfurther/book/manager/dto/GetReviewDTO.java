package strayfurther.book.manager.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetReviewDTO {
    private Long id;
    private String content;
    private Instant date;
    private Integer rating;
    private Long bookId;
}
