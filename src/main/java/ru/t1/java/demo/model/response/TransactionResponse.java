package ru.t1.java.demo.model.response;

import java.time.Instant;

public record TransactionResponse(Long id, String transactionId, String accountId, Long amount, Instant time, String status) {
}
