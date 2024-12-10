package ru.t1.java.service2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransactionMessage(
        Long clientId,
        Long accountId,
        Long transactionId,
        Instant timestamp,
        Long amount,
        Long balance
) {}


