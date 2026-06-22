package strayfurther.book.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetBookDTO {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Integer publicationYear;
    private String description;
    @Builder.Default
    private List<GetReviewDTO> reviews = new ArrayList<>();

}
