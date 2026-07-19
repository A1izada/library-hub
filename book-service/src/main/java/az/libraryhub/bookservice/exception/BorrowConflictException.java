package az.libraryhub.bookservice.exception;


public class BorrowConflictException extends RuntimeException {
    public BorrowConflictException(String message) {
        super(message);
    }
}
