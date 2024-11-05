package ru.t1.java.demo.model;

public record AccountResponse(Long id, Long clientId, String accountType, Long balance) {
}
