package ru.t1.java.service2.dto;

public record TransactionResultMessage(
        Long clientId,
        Long accountId,
        Long transactionId,
        String status) {}
