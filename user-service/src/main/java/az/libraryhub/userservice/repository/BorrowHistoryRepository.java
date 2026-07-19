package az.libraryhub.userservice.repository;

import az.libraryhub.userservice.entity.BorrowHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BorrowHistoryRepository extends JpaRepository<BorrowHistory, Long> {

    Optional<BorrowHistory> findFirstByUserIdAndBookIdAndStatus(Long userId, Long bookId, String status);
    List<BorrowHistory> findByUserId(Long userId);
}
