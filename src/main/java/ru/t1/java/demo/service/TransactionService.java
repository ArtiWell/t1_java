package ru.t1.java.demo.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.emums.AccountStatus;
import ru.t1.java.demo.emums.TransactionStatus;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.entity.TransactionEntity;
import ru.t1.java.demo.exception.EntityNotFoundException;
import ru.t1.java.demo.model.TransactionRequest;
import ru.t1.java.demo.model.TransactionResponse;
import ru.t1.java.demo.model.dto.TransactionMessageDTO;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;

import static ru.t1.java.demo.mapper.TransactionMapper.TRANSACTION_MAPPER;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, TransactionMessageDTO> kafkaTemplate;


    public Page<TransactionResponse> getAllTransaction(Pageable pageable) {

        return transactionRepository.findAll(pageable)
                .map(TRANSACTION_MAPPER::toResponse);
    }


    @Transactional
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) {
        AccountEntity account = accountRepository.findById(transactionRequest.accountId())
                .orElseThrow(() -> new EntityNotFoundException("Account with ID " + transactionRequest.accountId() + " not found"));

        if (account.getStatus() != AccountStatus.OPEN) {
            throw new IllegalStateException("Account with ID " + transactionRequest.accountId() + " is not open for transactions.");
        }

        account.setBalance(account.getBalance() + transactionRequest.amount());
        TransactionEntity transaction = TRANSACTION_MAPPER.toEntity(transactionRequest, account);
        transaction.setStatus(TransactionStatus.REQUESTED);
        transactionRepository.save(transaction);

        sendTransactionAcceptMessage(account, transaction);

        return TRANSACTION_MAPPER.toResponse(transaction);
    }

    private void sendTransactionAcceptMessage(AccountEntity account, TransactionEntity transaction) {
        TransactionMessageDTO message = new TransactionMessageDTO(
                account.getClient().getId(),
                account.getId(),
                transaction.getId(),
                transaction.getTime(),
                transaction.getAmount(),
                account.getBalance()
        );

        kafkaTemplate.send("t1_demo_transaction_accept", message);
        log.info("Transaction accept message sent to Kafka for transaction ID {}", transaction.getId());
    }

    public TransactionResponse getTransactionById(Long id) {
        return TRANSACTION_MAPPER.toResponse(transactionRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Transaction with ID "+id+" not found")
        ));
    }
}
