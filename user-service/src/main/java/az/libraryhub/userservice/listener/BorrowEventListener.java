package az.libraryhub.userservice.listener;

import az.libraryhub.userservice.entity.BorrowHistory;
import az.libraryhub.userservice.dto.BorrowEvent;
import az.libraryhub.userservice.repository.BorrowHistoryRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class BorrowEventListener {

    private final BorrowHistoryRepository borrowHistoryRepository;

    public BorrowEventListener(BorrowHistoryRepository borrowHistoryRepository) {
        this.borrowHistoryRepository = borrowHistoryRepository;
    }

    @RabbitListener(queues = "borrow.events.queue")
    public void handleBorrowEvent(BorrowEvent event) {
        if ("BORROWED".equals(event.getEventType())) {
            BorrowHistory history = new BorrowHistory();
            history.setUserId(event.getUserId());
            history.setBookId(event.getBookId());
            history.setBookTitle(event.getBookTitle());
            history.setBorrowedAt(event.getTimestamp());
            history.setStatus("ACTIVE");
            borrowHistoryRepository.save(history);

        } else if ("RETURNED".equals(event.getEventType())) {
            borrowHistoryRepository
                    .findFirstByUserIdAndBookIdAndStatus(event.getUserId(), event.getBookId(), "ACTIVE")
                    .ifPresent(history -> {
                        history.setStatus("RETURNED");
                        history.setReturnedAt(event.getTimestamp());
                        borrowHistoryRepository.save(history);
                    });
        }
    }
}
