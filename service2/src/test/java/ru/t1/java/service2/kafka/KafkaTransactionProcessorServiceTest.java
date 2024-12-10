package ru.t1.java.service2.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import ru.t1.java.service2.dto.TransactionMessage;
import ru.t1.java.service2.dto.TransactionResultMessage;
import ru.t1.java.service2.entity.AccountEntity;
import ru.t1.java.service2.entity.TransactionEntity;
import ru.t1.java.service2.repository.TransactionRepository;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaTransactionProcessorServiceTest {
    @Mock
    private KafkaTemplate<String, TransactionResultMessage> kafkaTemplate;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private KafkaTransactionProcessorService kafkaTransactionProcessorService;

    @Test
    void shouldProcessAcceptedTransaction() {
        TransactionMessage message = new TransactionMessage(1L, 100L, 123L, Instant.now(), 500L, 100L);
        AccountEntity account = new AccountEntity();
        account.setId(1L);

        kafkaTransactionProcessorService.processTransaction(message);

        verify(kafkaTemplate).send(eq("t1_demo_transaction_result"), any(TransactionResultMessage.class));
        verify(transactionRepository, never()).save(any(TransactionEntity.class));
    }

    @Test
    void shouldProcessRejectedTransaction() {
        TransactionMessage message = new TransactionMessage(1L, 100L, 123L, Instant.now(), 500L, 100L);

        kafkaTransactionProcessorService.processTransaction(message);

        verify(kafkaTemplate).send(eq("t1_demo_transaction_result"), argThat(result -> result.status().equals("REJECTED")));
    }

}