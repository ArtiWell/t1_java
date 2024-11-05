package ru.t1.java.demo.model;

import ru.t1.java.demo.emums.AccountType;

public record AccountRequest(Long clientId, AccountType accountType, Long balance) {
}
