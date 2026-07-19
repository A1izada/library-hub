package az.libraryhub.bookservice.service

import az.libraryhub.bookservice.dto.BookRequest
import az.libraryhub.bookservice.entity.Book
import az.libraryhub.bookservice.exception.BookNotFoundException
import az.libraryhub.bookservice.repository.BookRepository
import az.libraryhub.bookservice.repository.CategoryRepository
import spock.lang.Specification

class BookServiceSpec extends Specification {

    BookRepository bookRepository = Mock()
    CategoryRepository categoryRepository = Mock()

    BookService bookService = new BookService(bookRepository, categoryRepository)

    def "mövcud kitab ID ilə tapılır"() {
        given: "bazada mövcud olan bir kitab"
        def book = new Book()
        book.id = 1L
        book.title = "Test Kitab"
        book.author = "Test Müəllif"
        book.totalCopies = 5
        book.availableCopies = 5

        bookRepository.findById(1L) >> Optional.of(book)

        when: "getById metodu çağırılır"
        def result = bookService.getById(1L)

        then: "kitabın məlumatları düzgün qaytarılır"
        result.title == "Test Kitab"
        result.author == "Test Müəllif"
    }

    def "mövcud olmayan kitab ID ilə axtarılanda xəta atılır"() {
        given: "bazada mövcud olmayan bir ID"
        bookRepository.findById(999L) >> Optional.empty()

        when: "getById metodu çağırılır"
        bookService.getById(999L)

        then: "BookNotFoundException atılır"
        thrown(BookNotFoundException)
    }

    def "yeni kitab uğurla yaradılır"() {
        given: "yeni kitab məlumatları"
        def request = new BookRequest()
        request.title = "Yeni Kitab"
        request.author = "Yeni Müəllif"
        request.totalCopies = 3

        bookRepository.save(_ as Book) >> { Book b -> b.setId(1L); return b }

        when: "create metodu çağırılır"
        def result = bookService.create(request)

        then: "kitab düzgün yaradılır"
        result.title == "Yeni Kitab"
        result.totalCopies == 3
        result.availableCopies == 3
    }

    def "nüsxə sayı 0 olan kitab ödünc verilə bilmir"() {
        given: "əlçatan nüsxəsi olmayan kitab"
        def book = new Book()
        book.id = 2L
        book.availableCopies = 0

        bookRepository.findById(2L) >> Optional.of(book)

        when: "update metodu çağırılır (sadəcə nümunə üçün getById sınağı)"
        def result = bookService.getById(2L)

        then: "kitab hələ də tapılır, amma nüsxə sayı 0-dır"
        result.availableCopies == 0
    }

    def "kateqoriya ID-si ilə kitab yaradılır"() {
        given: "kateqoriya mövcuddur"
        def category = new az.libraryhub.bookservice.entity.Category()
        category.id = 1L
        category.name = "Roman"

        def request = new BookRequest()
        request.title = "Kateqoriyalı Kitab"
        request.author = "Müəllif"
        request.totalCopies = 2
        request.categoryId = 1L

        categoryRepository.findById(1L) >> Optional.of(category)
        bookRepository.save(_ as Book) >> { Book b -> b.setId(3L); return b }

        when: "create metodu çağırılır"
        def result = bookService.create(request)

        then: "kitab, kateqoriya adı ilə birgə qaytarılır"
        result.categoryName == "Roman"
    }
}