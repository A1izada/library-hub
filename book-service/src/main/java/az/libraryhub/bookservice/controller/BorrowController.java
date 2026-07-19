package az.libraryhub.bookservice.controller;


import az.libraryhub.bookservice.dto.BorrowRequest;
import az.libraryhub.bookservice.dto.BorrowResponse;
import az.libraryhub.bookservice.service.BorrowService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @PostMapping
    public ResponseEntity<BorrowResponse> borrow(
            Authentication authentication,
            @Valid @RequestBody BorrowRequest request) {
        Long userId = Long.parseLong(authentication.getName());
        BorrowResponse response = borrowService.borrowBook(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<BorrowResponse> returnBook(
            Authentication authentication,
            @PathVariable Long id) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(borrowService.returnBook(userId, id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<BorrowResponse>> getMyBorrows(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(borrowService.getMyBorrows(userId));
    }
}
