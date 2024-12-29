package ru.t1.service3.service;

import org.springframework.stereotype.Service;
import ru.t1.service3.dto.AccountUnblockRequest;
import ru.t1.service3.dto.ClientUnblockRequest;

@Service
public class UnblockService {
    public boolean unblockClient(ClientUnblockRequest request) {
        return Math.random() > 0.5;
    }

    public boolean unblockAccount(AccountUnblockRequest request) {
        return true;
    }
}