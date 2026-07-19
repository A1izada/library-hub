package az.libraryhub.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private List<FieldError> errors;

    @Getter
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }
}
