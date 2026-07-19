package az.libraryhub.bookservice.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {

    @NotBlank(message = "Kateqoriya adı boş ola bilməz")
    private String name;

    private String description;
}
