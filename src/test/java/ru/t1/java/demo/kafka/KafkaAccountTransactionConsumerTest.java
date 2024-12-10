package ru.t1.java.demo.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import ru.t1.java.demo.dao.persistence.AccountRepository;
import ru.t1.java.demo.dao.persistence.TransactionRepository;
import ru.t1.java.demo.emums.AccountStatus;
import ru.t1.java.demo.emums.TransactionStatus;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.entity.TransactionEntity;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaAccountTransactionConsumerTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private KafkaAccountTransactionConsumer kafkaConsumer;
    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void shouldProcessValidAccountMessages() throws Exception {
        String validMessage = "{\"accountId\":1,\"status\":\"OPEN\"}";
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAccountId("1");
        accountEntity.setStatus(AccountStatus.OPEN);

        when(objectMapper.readValue(validMessage, AccountEntity.class)).thenReturn(accountEntity);

        kafkaConsumer.listenAccount(List.of(validMessage), acknowledgment, "account_topic");

        verify(accountRepository).saveAll(List.of(accountEntity));
        verify(acknowledgment).acknowledge();
    }


    @Test
    void shouldProcessValidTransactionMessages() throws Exception {
        String validMessage = "{\"transactionId\":1,\"status\":\"SUCCESS\"}";
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTransactionId("1");
        transactionEntity.setStatus(TransactionStatus.ACCEPTED);

        when(objectMapper.readValue(validMessage, TransactionEntity.class)).thenReturn(transactionEntity);

        kafkaConsumer.listenTransaction(List.of(validMessage), acknowledgment, "transaction_topic");

        verify(transactionRepository).saveAll(List.of(transactionEntity));
        verify(acknowledgment).acknowledge();
    }

}