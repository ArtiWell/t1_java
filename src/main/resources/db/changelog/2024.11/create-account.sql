-- liquibase formatted sql
-- changeset a_stol:1726476397331-1

CREATE TABLE IF NOT EXISTS account (
id BIGSERIAL PRIMARY KEY,
account_id UUID NOT NULL UNIQUE,
client_id BIGINT NOT NULL,
account_type VARCHAR(255) NOT NULL,
status VARCHAR(255) NOT NULL,
balance BIGINT NOT NULL,
frozen_amount BIGINT DEFAULT 0,
FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE
);