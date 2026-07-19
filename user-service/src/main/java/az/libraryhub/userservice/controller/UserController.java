package az.libraryhub.userservice.controller;

import az.libraryhub.userservice.dto.BorrowHistoryResponse;
import az.libraryhub.userservice.dto.UpdateProfileRequest;
import az.libraryhub.userservice.dto.UserResponse;
import az.libraryhub.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        UserResponse response = userService.getByUsername(username);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        String username = authentication.getName();
        UserResponse response = userService.updateProfile(username, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/borrows")
    public ResponseEntity<List<BorrowHistoryResponse>> getMyBorrowHistory(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getBorrowHistoryByUsername(username));
    }

}