--liquibase formatted sql
--changeset vugar:1
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       full_name VARCHAR(100),
                       role VARCHAR(20) NOT NULL DEFAULT 'USER',
                       is_active BOOLEAN NOT NULL DEFAULT true,
                       created_at TIMESTAMP NOT NULL DEFAULT now(),
                       updated_at TIMESTAMP
);