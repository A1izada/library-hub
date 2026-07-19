--liquibase formatted sql

--changeset vugar:002-create-books-table
CREATE TABLE books (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       isbn VARCHAR(20) UNIQUE,
                       category_id BIGINT,
                       total_copies INTEGER NOT NULL,
                       available_copies INTEGER NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT now(),
                       CONSTRAINT fk_book_category FOREIGN KEY (category_id) REFERENCES categories(id)
);