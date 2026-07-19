package az.libraryhub.bookservice.service;

import az.libraryhub.bookservice.dto.BookRequest;
import az.libraryhub.bookservice.dto.BookResponse;
import az.libraryhub.bookservice.entity.Book;
import az.libraryhub.bookservice.entity.Category;
import az.libraryhub.bookservice.exception.BookNotFoundException;
import az.libraryhub.bookservice.repository.BookRepository;
import az.libraryhub.bookservice.repository.CategoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    @Cacheable(value = "books", key = "#id")
    public BookResponse getById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Kitab tapılmadı, id: " + id));
        return toResponse(book);
    }

    public Page<BookResponse> search(String title, String author, Long categoryId, Pageable pageable) {
        return bookRepository.search(title, author, categoryId, pageable)
                .map(this::toResponse);
    }

    @CacheEvict(value = "books", allEntries = true)
    public BookResponse create(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getTotalCopies());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BookNotFoundException("Kateqoriya tapılmadı, id: " + request.getCategoryId()));
            book.setCategory(category);
        }

        Book saved = bookRepository.save(book);
        return toResponse(saved);
    }

    @CacheEvict(value = "books", key = "#id")
    public BookResponse update(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Kitab tapılmadı, id: " + id));

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BookNotFoundException("Kateqoriya tapılmadı, id: " + request.getCategoryId()));
            book.setCategory(category);
        }

        Book saved = bookRepository.save(book);
        return toResponse(saved);
    }

    @CacheEvict(value = "books", key = "#id")
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Kitab tapılmadı, id: " + id);
        }
        bookRepository.deleteById(id);
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getCategory() != null ? book.getCategory().getName() : null,
                book.getTotalCopies(),
                book.getAvailableCopies()
        );
    }
}
