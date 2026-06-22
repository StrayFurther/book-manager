package strayfurther.book.manager.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookDTO {
    @Size(max = 255) private String title;
    @Size(max = 255) private String author;
    @Size(max = 5000) private String description;
}
