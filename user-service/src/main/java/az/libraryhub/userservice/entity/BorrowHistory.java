package az.libraryhub.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "book_title", nullable = false, length = 255)
    private String bookTitle;

    @Column(name = "borrowed_at", nullable = false)
    private LocalDateTime borrowedAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Column(nullable = false, length = 20)
    private String status;
}