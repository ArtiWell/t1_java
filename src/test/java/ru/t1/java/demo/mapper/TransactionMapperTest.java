package ru.t1.java.demo.mapper;

import org.junit.jupiter.api.Test;
import ru.t1.java.demo.emums.TransactionStatus;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.entity.TransactionEntity;
import ru.t1.java.demo.model.reqest.TransactionRequest;
import ru.t1.java.demo.model.response.TransactionResponse;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TransactionMapperTest {

    private final TransactionMapper mapper = TransactionMapper.TRANSACTION_MAPPER;

    @Test
    void shouldMapEntityToResponseCorrectly() {
        AccountEntity account = new AccountEntity();
        account.setAccountId("123");

        TransactionEntity transaction = new TransactionEntity();
        transaction.setId(1L);
        transaction.setTransactionId("456");
        transaction.setAccount(account);
        transaction.setAmount(1000L);
        transaction.setTime(Instant.now());
        transaction.setStatus(TransactionStatus.ACCEPTED);

        TransactionResponse response = mapper.toResponse(transaction);

        assertNotNull(response, "Response should not be null");
        assertEquals(1L, response.id());
        assertEquals("456", response.transactionId());
        assertEquals("123", response.accountId());
        assertEquals(1000L, response.amount());
        assertEquals(transaction.getTime(), response.time());
        assertEquals("ACCEPTED", response.status());
    }

    @Test
    void shouldMapRequestToEntityCorrectly() {
        TransactionRequest request = new TransactionRequest(1L, 500L, "ACCEPTED");

        AccountEntity account = new AccountEntity();
        account.setId(1L);

        TransactionEntity entity = mapper.toEntity(request, account);

        assertNotNull(entity, "Entity should not be null");
        assertEquals(account, entity.getAccount());
        assertEquals(500L, entity.getAmount());
        assertEquals(TransactionStatus.ACCEPTED, entity.getStatus());
        assertNotNull(entity.getTime(), "Time should be set automatically");
        assertNull(entity.getTransactionId(), "Transaction ID should be null for new entity");
        assertNull(entity.getId(), "Entity ID should be null for new entity");
    }


    @Test
    void shouldHandleNullAccountGracefullyInEntityToResponse() {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setId(2L);
        transaction.setTransactionId("456");
        transaction.setAmount(2000L);
        transaction.setTime(Instant.now());
        transaction.setStatus(TransactionStatus.REJECTED);

        TransactionResponse response = mapper.toResponse(transaction);

        assertNotNull(response, "Response should not be null");
        assertEquals(2L, response.id());
        assertEquals("456", response.transactionId());
        assertNull(response.accountId(), "Account ID should be null");
        assertEquals(2000L, response.amount());
        assertEquals(transaction.getTime(), response.time());
        assertEquals("REJECTED", response.status());
    }
}