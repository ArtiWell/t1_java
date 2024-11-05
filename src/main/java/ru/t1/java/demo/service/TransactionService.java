package ru.t1.java.demo.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.entity.TransactionEntity;
import ru.t1.java.demo.exception.EntityNotFoundException;
import ru.t1.java.demo.model.TransactionRequest;
import ru.t1.java.demo.model.TransactionResponse;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;

import static ru.t1.java.demo.mapper.TransactionMapper.TRANSACTION_MAPPER;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;


    public Page<TransactionResponse> getAllTransaction(Pageable pageable) {

        return transactionRepository.findAll(pageable)
                .map(TRANSACTION_MAPPER::toResponse);
    }


    @Transactional
    public TransactionResponse createdTransaction(TransactionRequest transactionRequest) {

        AccountEntity account = accountRepository.findById(transactionRequest.accountId()).orElseThrow(
                ()->new EntityNotFoundException("Account with ID "+transactionRequest.accountId()+" not found")
        );
        account.setBalance(account.getBalance() + transactionRequest.amount());
        TransactionEntity transaction = TRANSACTION_MAPPER.toEntity(transactionRequest, account);
        transactionRepository.save(transaction);
        return TRANSACTION_MAPPER.toResponse(transaction);

    }

    public TransactionResponse getTransactionById(Long id) {
        return TRANSACTION_MAPPER.toResponse(transactionRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Transaction with ID "+id+" not found")
        ));
    }
}
