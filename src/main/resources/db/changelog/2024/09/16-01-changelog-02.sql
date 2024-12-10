-- liquibase formatted sql

-- changeset e_cha:1726477659739-1
ALTER TABLE client
    ADD client_id UUID NOT NULL UNIQUE;
ALTER TABLE client
    ADD first_name VARCHAR(255);
ALTER TABLE client
    ADD last_name VARCHAR(255);
ALTER TABLE client
    ADD middle_name VARCHAR(255);
ALTER TABLE client
    ADD is_blocked BOOLEAN NOT NULL DEFAULT FALSE;
