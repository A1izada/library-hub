package az.libraryhub.bookservice.job;

import az.libraryhub.bookservice.entity.Borrow;
import az.libraryhub.bookservice.repository.BorrowRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OverdueCheckJob {

    private static final Logger logger = LoggerFactory.getLogger(OverdueCheckJob.class);

    private final BorrowRepository borrowRepository;

    public OverdueCheckJob(BorrowRepository borrowRepository) {
        this.borrowRepository = borrowRepository;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void checkOverdueBorrows() {
        logger.info("Overdue yoxlanışı başladı: {}", LocalDateTime.now());

        List<Borrow> overdueBorrows = borrowRepository.findByStatusAndDueDateBefore("ACTIVE", LocalDateTime.now());

        if (overdueBorrows.isEmpty()) {
            logger.info("Vaxtı keçmiş borrow yoxdur");
            return;
        }

        for (Borrow borrow : overdueBorrows) {
            logger.warn("VAXTI KEÇİB: userId={}, bookId={}, dueDate={}",
                    borrow.getUserId(), borrow.getBook().getId(), borrow.getDueDate());
        }

        logger.info("Overdue yoxlanışı bitdi. Tapılan: {}", overdueBorrows.size());
    }
}
