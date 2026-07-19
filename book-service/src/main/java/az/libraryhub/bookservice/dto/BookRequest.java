package az.libraryhub.bookservice.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRequest {

    @NotBlank(message = "Başlıq boş ola bilməz")
    private String title;

    @NotBlank(message = "Müəllif adı boş ola bilməz")
    private String author;

    private String isbn;

    private Long categoryId;

    @Min(value = 1, message = "Nüsxə sayı ən azı 1 olmalıdır")
    private Integer totalCopies;
}
