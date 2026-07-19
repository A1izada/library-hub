package az.libraryhub.bookservice.repository;

import az.libraryhub.bookservice.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    List<Borrow> findByUserIdAndStatus(Long userId, String status);

    Optional<Borrow> findByUserIdAndBookIdAndStatus(Long userId, Long bookId, String status);

    List<Borrow> findByStatusAndDueDateBefore(String status, java.time.LocalDateTime dateTime);
}
