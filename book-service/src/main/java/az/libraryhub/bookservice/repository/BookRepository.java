package az.libraryhub.bookservice.repository;


import az.libraryhub.bookservice.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE " +
            "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', CAST(:title AS string), '%'))) AND " +
            "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', CAST(:author AS string), '%'))) AND " +
            "(:categoryId IS NULL OR b.category.id = :categoryId)")
    Page<Book> search(@Param("title") String title,
                      @Param("author") String author,
                      @Param("categoryId") Long categoryId,
                      Pageable pageable);
}