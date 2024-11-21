package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.model.reqest.TransactionRequest;
import ru.t1.java.demo.model.response.TransactionResponse;
import ru.t1.java.demo.service.TransactionService;

@RestController
@RequestMapping("transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService service;

    @GetMapping
    public Page<TransactionResponse> getAllTransaction(@PageableDefault Pageable pageable) {
        return service.getAllTransaction(pageable);
    }

    @PostMapping
    public TransactionResponse createdTransaction(@RequestBody TransactionRequest transactionRequest) {
        return service.createTransaction(transactionRequest);
    }

    @GetMapping("{id}")
    public TransactionResponse getTransactionById(@PathVariable Long id) {
        return service.getTransactionById(id);
    }


}
