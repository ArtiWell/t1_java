-- liquibase formatted sql
-- changeset a_stol:1726476397331-1

CREATE TABLE IF NOT EXISTS account (
id BIGSERIAL PRIMARY KEY,
client_id BIGINT NOT NULL,
account_type VARCHAR(255) NOT NULL,
balance BIGINT NOT NULL,
FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE
);