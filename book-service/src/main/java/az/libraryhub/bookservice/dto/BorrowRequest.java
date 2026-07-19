package az.libraryhub.bookservice.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BorrowRequest {

    @NotNull(message = "Kitab ID-si göstərilməlidir")
    private Long bookId;
}
