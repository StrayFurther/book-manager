package strayfurther.book.manager.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewDTO {
    @NotBlank
    private String content;

    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Integer rating;
}
