--liquibase formatted sql
--changeset vugar:2
CREATE TABLE borrow_history (
                                id BIGSERIAL PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                book_id BIGINT NOT NULL,
                                book_title VARCHAR(255) NOT NULL,
                                borrowed_at TIMESTAMP NOT NULL,
                                returned_at TIMESTAMP,
                                status VARCHAR(20) NOT NULL,
                                CONSTRAINT fk_borrow_user FOREIGN KEY (user_id) REFERENCES users(id)
);