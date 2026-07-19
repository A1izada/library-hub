package az.libraryhub.bookservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BorrowResponse {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime borrowedAt;
    private LocalDateTime dueDate;
    private String status;
}