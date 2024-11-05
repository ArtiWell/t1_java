package ru.t1.java.demo.model;

import java.time.Instant;

public record TransactionResponse(Long id, Long accountId, Long amount, Instant time) {
}
