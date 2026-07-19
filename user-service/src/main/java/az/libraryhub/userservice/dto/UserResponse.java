package az.libraryhub.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}
