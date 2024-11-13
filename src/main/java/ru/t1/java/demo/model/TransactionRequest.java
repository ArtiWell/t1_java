package ru.t1.java.demo.model;

public record TransactionRequest(Long accountId, Long amount, String status) {
}
