package ru.t1.java.demo.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import ru.t1.java.demo.model.dto.TransactionMessageDTO;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class KafkaTransactionProducerTest {
    @Mock
    private KafkaTemplate<String, TransactionMessageDTO> kafkaTemplate;
    @InjectMocks
    private KafkaTransactionProducer kafkaTransactionProducer;

    @Test
    void shouldSendTransactionAcceptMessage() {
        TransactionMessageDTO message = new TransactionMessageDTO(
                1L,
                2L,
                3L,
                Instant.now(),
                100L,
                1000L
        );

        kafkaTransactionProducer.sendTransactionAcceptMessage(message);

        ArgumentCaptor<TransactionMessageDTO> captor = ArgumentCaptor.forClass(TransactionMessageDTO.class);
        Mockito.verify(kafkaTemplate).send(eq("t1_demo_transaction_accept"), captor.capture());

        TransactionMessageDTO sentMessage = captor.getValue();
        assertEquals(message.clientId(), sentMessage.clientId());
        assertEquals(message.accountId(), sentMessage.accountId());
        assertEquals(message.transactionId(), sentMessage.transactionId());
        assertEquals(message.amount(), sentMessage.amount());
        assertEquals(message.balance(), sentMessage.balance());
        assertNotNull(sentMessage.timestamp());
    }
}