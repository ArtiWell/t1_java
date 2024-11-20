package ru.t1.java.demo.model;

public record AccountRequest(Long clientId, String accountType, String status, Long balance, Long frozenAmount) {
}
