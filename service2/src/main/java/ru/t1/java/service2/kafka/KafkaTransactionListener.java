package ru.t1.java.service2.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.service2.dto.TransactionMessage;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTransactionListener {

    private final KafkaTransactionProcessorService transactionProcessorService;

    @Value("${transaction.processing.time-window-millis}")
    private long timeWindowMillis;

    @Value("${transaction.processing.threshold}")
    private int transactionThreshold;

    @KafkaListener(topics = "t1_demo_transaction_accept", containerFactory = "transactionKafkaListenerContainerFactory")
    public void listenTransaction(@Payload List<TransactionMessage> messages, Acknowledgment ack) {
        log.info("Получено {} сообщений из топика t1_demo_transaction_accept", messages.size());
        messages.forEach(transactionProcessorService::processTransaction);
        ack.acknowledge();
    }
}
