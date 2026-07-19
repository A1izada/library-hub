package az.libraryhub.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BorrowHistoryResponse {
    private Long bookId;
    private String bookTitle;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;
    private String status;
}
