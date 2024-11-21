package ru.t1.java.demo.model.reqest;

public record TransactionRequest(Long accountId, Long amount, String status) {
}
