package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransactionMessageDTO(
        Long clientId,
        Long accountId,
        Long transactionId,
        Instant timestamp,
        Long amount,
        Long balance
) {}
