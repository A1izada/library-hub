--liquibase formatted sql

--changeset vugar:001-create-categories-table
CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            description VARCHAR(255)
);