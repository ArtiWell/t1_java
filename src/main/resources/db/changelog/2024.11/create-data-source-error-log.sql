-- liquibase formatted sql

-- changeset a_stol:1726676397331-1
CREATE TABLE IF NOT EXISTS data_source_error_log (
id BIGSERIAL PRIMARY KEY,
stack_trace TEXT,
message VARCHAR(255) NOT NULL,
method_signature VARCHAR(255) NOT NULL
);