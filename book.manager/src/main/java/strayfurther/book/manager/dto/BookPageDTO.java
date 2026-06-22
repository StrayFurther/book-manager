package strayfurther.book.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookPageDTO {
    private List<GetBookDTO> content;
    private int totalElements;
    private int totalPages;
    private int currentPage;
}

