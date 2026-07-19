--liquibase formatted sql

--changeset vugar:003-create-borrows-table
CREATE TABLE borrows (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         book_id BIGINT NOT NULL,
                         borrowed_at TIMESTAMP NOT NULL DEFAULT now(),
                         due_date TIMESTAMP NOT NULL,
                         returned_at TIMESTAMP,
                         status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                         CONSTRAINT fk_borrow_book FOREIGN KEY (book_id) REFERENCES books(id)
);
