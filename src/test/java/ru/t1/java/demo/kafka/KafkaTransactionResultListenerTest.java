package ru.t1.java.demo.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import ru.t1.java.demo.dao.persistence.AccountRepository;
import ru.t1.java.demo.dao.persistence.TransactionRepository;
import ru.t1.java.demo.emums.TransactionStatus;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.entity.TransactionEntity;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaTransactionResultListenerTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private KafkaTransactionResultListener kafkaTransactionResultListener;
    @Mock
    private Acknowledgment acknowledgment;
    @Mock
    private ConsumerRecord<String, String> consumerRecord;

    @Test
    void shouldProcessAcceptedTransaction() throws Exception {
        String message = "{\"transactionId\":1,\"status\":\"ACCEPTED\",\"account\":{\"id\":1}}";
        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionId("1");
        transaction.setStatus(TransactionStatus.ACCEPTED);

        AccountEntity account = new AccountEntity();
        account.setId(1L);
        transaction.setAccount(account);

        when(objectMapper.readValue(message, TransactionEntity.class)).thenReturn(transaction);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        kafkaTransactionResultListener.listenTransactionResult(message, acknowledgment, consumerRecord);

        verify(transactionRepository).save(transaction);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldProcessBlockedTransaction() throws Exception {
        String message = "{\"transactionId\":2,\"status\":\"BLOCKED\",\"amount\":100,\"account\":{\"id\":2}}";
        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionId("2");
        transaction.setStatus(TransactionStatus.BLOCKED);
        transaction.setAmount(100L);

        AccountEntity account = new AccountEntity();
        account.setId(2L);
        account.setFrozenAmount(0L);
        transaction.setAccount(account);

        when(objectMapper.readValue(message, TransactionEntity.class)).thenReturn(transaction);
        when(accountRepository.findById(2L)).thenReturn(Optional.of(account));

        kafkaTransactionResultListener.listenTransactionResult(message, acknowledgment, consumerRecord);

        verify(accountRepository).save(account);
        assertEquals(100L, account.getFrozenAmount());
        verify(transactionRepository).save(transaction);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldProcessRejectedTransaction() throws Exception {
        String message = "{\"transactionId\":3,\"status\":\"REJECTED\",\"amount\":50,\"account\":{\"id\":3}}";
        TransactionEntity transaction = new TransactionEntity();
        transaction.setTransactionId("3");
        transaction.setStatus(TransactionStatus.REJECTED);
        transaction.setAmount(50L);

        AccountEntity account = new AccountEntity();
        account.setId(3L);
        account.setBalance(200L);
        transaction.setAccount(account);

        when(objectMapper.readValue(message, TransactionEntity.class)).thenReturn(transaction);
        when(accountRepository.findById(3L)).thenReturn(Optional.of(account));

        kafkaTransactionResultListener.listenTransactionResult(message, acknowledgment, consumerRecord);

        verify(accountRepository).save(account);
        assertEquals(150L, account.getBalance());
        verify(transactionRepository).save(transaction);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void shouldSkipInvalidTransactionMessage() throws Exception {
        String invalidMessage = "{\"status\":\"ACCEPTED\"}";

        doAnswer(invocation -> {
            throw new IOException("Ошибка десериализации");
        }).when(objectMapper).readValue(eq(invalidMessage), eq(TransactionEntity.class));

        kafkaTransactionResultListener.listenTransactionResult(invalidMessage, acknowledgment, consumerRecord);

        verifyNoInteractions(accountRepository);
        verifyNoInteractions(transactionRepository);
        verify(acknowledgment).acknowledge();
    }
}