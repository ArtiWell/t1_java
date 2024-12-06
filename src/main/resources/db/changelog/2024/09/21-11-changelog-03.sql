-- liquibase formatted sql

-- changeset a_stol:1726477659739-6
ALTER TABLE client
    ADD is_blocked BOOLEAN NOT NULL DEFAULT FALSE;