package ru.t1.service3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.service3.dto.AccountUnblockRequest;
import ru.t1.service3.dto.ClientUnblockRequest;
import ru.t1.service3.service.UnblockService;

@RestController
@RequestMapping("/api/unblock")
@RequiredArgsConstructor
public class UnblockController {
    private final UnblockService unblockService;

    @PostMapping("/clients")
    public boolean unblockClient(@RequestBody ClientUnblockRequest request) {
        return unblockService.unblockClient(request);
    }

    @PostMapping("/accounts")
    public boolean unblockAccount(@RequestBody AccountUnblockRequest request) {
        return unblockService.unblockAccount(request);
    }
}
