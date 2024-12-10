package ru.t1.java.service2.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import ru.t1.java.service2.dto.TransactionMessage;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaTransactionListenerTest {
    @Mock
    private KafkaTransactionProcessorService transactionProcessorService;
    @Mock
    private Acknowledgment ack;
    @InjectMocks
    private KafkaTransactionListener kafkaTransactionListener;

    @Test
    void shouldProcessTransactionsAndAcknowledge() {
        List<TransactionMessage> messages = List.of(
                new TransactionMessage(1L, 100L, 123L, Instant.now(),500L, 100L),
                new TransactionMessage(2L, 200L, 456L, Instant.now(),1000L, 200L)
        );

        kafkaTransactionListener.listenTransaction(messages, ack);

        verify(transactionProcessorService, times(2)).processTransaction(any(TransactionMessage.class));
        verify(ack).acknowledge();
    }
}