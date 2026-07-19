package az.libraryhub.userservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "İstifadəçi adı boş ola bilməz")
    @Size(min = 3, max = 50, message = "İstifadəçi adı 3-50 simvol arası olmalıdır")
    private String username;

    @NotBlank(message = "Email boş ola bilməz")
    @Email(message = "Email formatı düzgün deyil")
    private String email;

    @NotBlank(message = "Parol boş ola bilməz")
    @Size(min = 8, message = "Parol minimum 8 simvol olmalıdır")
    private String password;

    @Size(max = 100, message = "Ad-soyad maksimum 100 simvol ola bilər")
    private String fullName;
}
