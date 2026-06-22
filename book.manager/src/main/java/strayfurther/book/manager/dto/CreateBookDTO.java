package strayfurther.book.manager.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author must be at most 255 characters")
    private String author;

    @NotBlank(message = "isbn is required")
    @Pattern(
            regexp = "^(97(8|9))?\\d{9}(\\d|X)$",
            message = "ISBN must be valid ISBN-10 or ISBN-13 format"
    )
    private String isbn;

    @NotNull
    @Min(value = 1450, message = "Publication year must be >= 1450")
    @Max(value = 2100, message = "Publication year must be <= 2100")
    private Integer publicationYear;

    @Size(max = 2000, message = "Description must be at most 2000 characters")
    private String description;

}
