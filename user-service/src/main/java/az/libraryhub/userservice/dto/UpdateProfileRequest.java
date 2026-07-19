package az.libraryhub.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

    @Email(message = "Email formatı düzgün deyil")
    private String email;

    @Size(max = 100, message = "Ad-soyad maksimum 100 simvol ola bilər")
    private String fullName;
}
