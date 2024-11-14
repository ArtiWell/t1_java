package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.TransactionMessageDTO;


@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaTransactionProducer {

    private final KafkaTemplate<String, TransactionMessageDTO> kafkaTemplate;

    private final String transactionAcceptTopic = "t1_demo_transaction_accept";

    public void sendTransactionAcceptMessage(TransactionMessageDTO message) {
        kafkaTemplate.send(transactionAcceptTopic, message);
        log.info("Transaction accept message sent to Kafka: {}", message);
    }
}
