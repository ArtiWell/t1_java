package ru.t1.java.demo.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.emums.AccountStatus;
import ru.t1.java.demo.emums.TransactionStatus;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.entity.TransactionEntity;
import ru.t1.java.demo.dao.persistence.AccountRepository;
import ru.t1.java.demo.dao.persistence.TransactionRepository;


import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaTransactionResultListener {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${t1.kafka.topic.transaction_result}", groupId = "${t1.kafka.consumer.group-id}.transaction_result")
    public void listenTransactionResult(@Payload String message, Acknowledgment ack, ConsumerRecord<String, String> record) {
        log.info("Received message from topic {}: {}", record.topic(), message);

        try {
            TransactionEntity transaction = objectMapper.readValue(message, TransactionEntity.class);
            if (transaction.getTransactionId() == null || transaction.getStatus() == null) {
                log.warn("Получено сообщение без transactionId или статуса: {}", message);
                return;
            }

            Optional<AccountEntity> accountOpt = accountRepository.findById(transaction.getAccount().getId());
            accountOpt.ifPresent(account -> processTransactionByStatus(transaction, account));

            ack.acknowledge();
        } catch (IOException e) {
            log.error("Ошибка при десериализации TransactionEntity: {}", e.getMessage());
        }
    }

    private void processTransactionByStatus(TransactionEntity transaction, AccountEntity account) {
        switch (transaction.getStatus()) {
            case ACCEPTED -> updateTransactionStatus(transaction);
            case BLOCKED -> processBlockedTransaction(transaction, account);
            case REJECTED -> processRejectedTransaction(transaction, account);
            default -> log.warn("Неизвестный статус транзакции: {}", transaction.getStatus());
        }
    }

    private void updateTransactionStatus(TransactionEntity transaction) {
        transaction.setStatus(TransactionStatus.ACCEPTED);
        transactionRepository.save(transaction);
        log.info("Transaction with ID {} updated to status ACCEPTED", transaction.getId());
    }

    private void processBlockedTransaction(TransactionEntity transaction, AccountEntity account) {
        account.setStatus(AccountStatus.BLOCKED);
        account.setFrozenAmount(account.getFrozenAmount() + transaction.getAmount());
        accountRepository.save(account);

        transaction.setStatus(TransactionStatus.BLOCKED);
        transactionRepository.save(transaction);
        log.info("Transaction with ID {} blocked. Account ID {} updated with frozen amount {}", transaction.getId(), account.getId(), account.getFrozenAmount());
    }

    private void processRejectedTransaction(TransactionEntity transaction, AccountEntity account) {
        account.setBalance(account.getBalance() - transaction.getAmount());
        accountRepository.save(account);

        transaction.setStatus(TransactionStatus.REJECTED);
        transactionRepository.save(transaction);
        log.info("Transaction with ID {} rejected. Account ID {} balance updated to {}", transaction.getId(), account.getId(), account.getBalance());
    }
}
