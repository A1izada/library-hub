package az.libraryhub.bookservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class BookResponse implements Serializable {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String categoryName;
    private Integer totalCopies;
    private Integer availableCopies;
}