package ru.t1.java.demo.model;

public record AccountResponse(Long id, Long clientId, String accountId, String accountType, String status, Long balance, Long frozenAmount) {
}