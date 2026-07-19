package az.libraryhub.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowEvent implements Serializable {
    private String eventType;
    private Long userId;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime timestamp;
}
