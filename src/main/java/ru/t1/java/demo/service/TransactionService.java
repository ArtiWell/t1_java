package ru.t1.java.demo.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import ru.t1.java.demo.dao.persistence.AccountRepository;
import ru.t1.java.demo.dao.persistence.TransactionRepository;
import ru.t1.java.demo.emums.AccountStatus;
import ru.t1.java.demo.emums.TransactionStatus;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.entity.TransactionEntity;
import ru.t1.java.demo.exception.EntityNotFoundException;
import ru.t1.java.demo.model.dto.TransactionMessageDTO;
import ru.t1.java.demo.model.reqest.TransactionRequest;
import ru.t1.java.demo.model.response.TransactionResponse;

import javax.crypto.SecretKey;
import java.util.Date;

import static ru.t1.java.demo.mapper.TransactionMapper.TRANSACTION_MAPPER;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, TransactionMessageDTO> kafkaTemplate;
    private final RestClient restClient;

    @Value("${transaction.reject-threshold}")
    private int rejectThreshold;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;


    @Transactional
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) {
        AccountEntity account = accountRepository.findById(transactionRequest.accountId())
                .orElseThrow(() -> new EntityNotFoundException("Account with ID " + transactionRequest.accountId() + " not found"));

        if (account.getStatus() != AccountStatus.OPEN) {
            throw new IllegalStateException("Account with ID " + transactionRequest.accountId() + " is not open for transactions.");
        }

        boolean isClientBlocked = account.getClient().isBlocked();

        if (isClientBlocked) {
            handleBlockedClient(account);
            return saveRejectedTransaction(account, transactionRequest.amount());
        }
        isClientBlocked = checkClientStatus(account.getClient().getId());

        if (isClientBlocked) {
            handleBlockedClient(account);
            return saveRejectedTransaction(account, transactionRequest.amount());
        }

        long rejectedCount = transactionRepository.countByAccountAndStatus(account, TransactionStatus.REJECTED);

        if (rejectedCount >= rejectThreshold) {
            handleArrestedAccount(account);
            return saveRejectedTransaction(account, transactionRequest.amount());
        }

        TransactionEntity transaction = TRANSACTION_MAPPER.toEntity(transactionRequest, account);
        sendTransactionAcceptMessage(account, transaction);

        return processTransaction(account, transactionRequest, transaction);
    }

    private boolean checkClientStatus(Long clientId) {
        String url = "/api/check-client";
        log.info("Checking client status for client ID {}.", clientId);

        return Boolean.TRUE.equals(restClient.get()
                .uri(uriBuilder -> uriBuilder.path(url).queryParam("clientId", clientId).build())
                .header("Authorization", "Bearer " + generateJwtToken())
                .retrieve()
                .body(Boolean.class));
    }

    private void handleBlockedClient(AccountEntity account) {
        account.getClient().setBlocked(true);
        account.setStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);
        log.info("Client ID {} and account ID {} marked as BLOCKED.", account.getClient().getId(), account.getId());
    }

    private void handleArrestedAccount(AccountEntity account) {
        account.setStatus(AccountStatus.ARRESTED);
        accountRepository.save(account);
        log.info("Account ID {} marked as ARRESTED.", account.getId());
    }

    private TransactionResponse saveRejectedTransaction(AccountEntity account, Long amount) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.REJECTED);
        transactionRepository.save(transaction);
        log.info("Transaction for account ID {} rejected.", account.getId());
        return TRANSACTION_MAPPER.toResponse(transaction);
    }

    private TransactionResponse processTransaction(AccountEntity account, TransactionRequest request, TransactionEntity transaction) {
        transaction.setStatus(TransactionStatus.ACCEPTED);
        account.setBalance(account.getBalance() + request.amount());
        transactionRepository.save(transaction);
        accountRepository.save(account);
        log.info("Transaction for account ID {} accepted.", account.getId());
        return TRANSACTION_MAPPER.toResponse(transaction);
    }

    private String generateJwtToken() {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        return Jwts.builder()
                .setSubject("service1")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
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
                () -> new EntityNotFoundException("Transaction with ID " + id + " not found")
        ));
    }

    public Page<TransactionResponse> getAllTransaction(Pageable pageable) {

        return transactionRepository.findAll(pageable)
                .map(TRANSACTION_MAPPER::toResponse);
    }
}
