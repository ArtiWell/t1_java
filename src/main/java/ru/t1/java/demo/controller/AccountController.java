package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.model.AccountRequest;
import ru.t1.java.demo.model.AccountResponse;
import ru.t1.java.demo.service.AccountService;

@RestController
@RequestMapping("accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;


    @GetMapping
    public Page<AccountResponse> getAllAccounts(@PageableDefault Pageable pageable) {
        return service.getAllAccounts(pageable);
    }

    @PostMapping
    public AccountResponse createdAccount(@RequestBody AccountRequest accountDTO) {
        return service.createdAccount(accountDTO);
    }

    @GetMapping("{id}")
    public AccountResponse getAccountById(@PathVariable Long id) {
        return service.getAccountById(id);
    }

    @DeleteMapping("{id}")
    public void deleteAccount(@PathVariable Long id) {
        service.deleteAccount(id);
    }


}
