package az.libraryhub.userservice.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "İstifadəçi adı boş ola bilməz")
    private String username;

    @NotBlank(message = "Parol boş ola bilməz")
    private String password;
}
