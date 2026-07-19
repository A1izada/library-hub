package az.libraryhub.bookservice.service;

import org.springframework.cache.CacheManager;
import az.libraryhub.bookservice.dto.BorrowRequest;
import az.libraryhub.bookservice.dto.BorrowResponse;
import az.libraryhub.bookservice.entity.Book;
import az.libraryhub.bookservice.entity.Borrow;
import az.libraryhub.bookservice.exception.BookNotFoundException;
import az.libraryhub.bookservice.exception.BorrowConflictException;
import az.libraryhub.bookservice.repository.BookRepository;
import az.libraryhub.bookservice.repository.BorrowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final CacheManager cacheManager;
    private final BorrowEventPublisher eventPublisher;

    public BorrowService(BorrowRepository borrowRepository, BookRepository bookRepository,
                         CacheManager cacheManager, BorrowEventPublisher eventPublisher) {
        this.borrowRepository = borrowRepository;
        this.bookRepository = bookRepository;
        this.cacheManager = cacheManager;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public BorrowResponse borrowBook(Long userId, BorrowRequest request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Kitab tapılmadı, id: " + request.getBookId()));

        if (book.getAvailableCopies() <= 0) {
            throw new BorrowConflictException("Bu kitabdan hazırda əlçatan nüsxə yoxdur");
        }

        boolean alreadyBorrowed = borrowRepository
                .findByUserIdAndBookIdAndStatus(userId, book.getId(), "ACTIVE")
                .isPresent();
        if (alreadyBorrowed) {
            throw new BorrowConflictException("Siz artıq bu kitabı götürmüsünüz, əvvəlcə onu geri qaytarın");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        cacheManager.getCache("books").evict(book.getId());

        Borrow borrow = new Borrow();
        borrow.setUserId(userId);
        borrow.setBook(book);
        borrow.setBorrowedAt(LocalDateTime.now());
        borrow.setDueDate(LocalDateTime.now().plusDays(14));
        borrow.setStatus("ACTIVE");

        Borrow saved = borrowRepository.save(borrow);
        eventPublisher.publishBorrowEvent(userId, book.getId(), book.getTitle());

        return toResponse(saved);
    }

    @Transactional
    public BorrowResponse returnBook(Long userId, Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new BookNotFoundException("Borrow qeydi tapılmadı, id: " + borrowId));

        if (!borrow.getUserId().equals(userId)) {
            throw new BorrowConflictException("Bu borrow qeydi sizə aid deyil");
        }
        if (!"ACTIVE".equals(borrow.getStatus())) {
            throw new BorrowConflictException("Bu kitab artıq qaytarılıb");
        }

        borrow.setStatus("RETURNED");
        borrow.setReturnedAt(LocalDateTime.now());
        borrowRepository.save(borrow);

        Book book = borrow.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        cacheManager.getCache("books").evict(book.getId());
        eventPublisher.publishReturnEvent(userId, book.getId(), book.getTitle());

        return toResponse(borrow);
    }

    public List<BorrowResponse> getMyBorrows(Long userId) {
        return borrowRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private BorrowResponse toResponse(Borrow borrow) {
        return new BorrowResponse(
                borrow.getId(),
                borrow.getBook().getId(),
                borrow.getBook().getTitle(),
                borrow.getBorrowedAt(),
                borrow.getDueDate(),
                borrow.getStatus()
        );
    }
}
