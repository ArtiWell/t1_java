package ru.t1.java.demo.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.entity.AccountEntity;
import ru.t1.java.demo.entity.TransactionEntity;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaAccountTransactionConsumer {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(id = "${t1.kafka.consumer.group-id}.account",
            topics = "${t1.kafka.topic.account}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenAccount(@Payload List<String> messages,
                              Acknowledgment ack,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.debug("Account consumer: Обработка новых сообщений в топике {}", topic);
        try {
            List<AccountEntity> accounts = messages.stream()
                    .map(msg -> {
                        try {
                            return objectMapper.readValue(msg, AccountEntity.class);
                        } catch (IOException e) {
                            log.error("Ошибка при десериализации AccountEntity: {}", e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
            accountRepository.saveAll(accounts);
            log.debug("Account consumer: записи сохранены");
        } finally {
            ack.acknowledge();
        }
    }

    @KafkaListener(id = "${t1.kafka.consumer.group-id}.transaction",
            topics = "${t1.kafka.topic.transaction}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenTransaction(@Payload List<String> messages,
                                  Acknowledgment ack,
                                  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.debug("Transaction consumer: Обработка новых сообщений в топике {}", topic);
        try {
            List<TransactionEntity> transactions = messages.stream()
                    .map(msg -> {
                        try {
                            return objectMapper.readValue(msg, TransactionEntity.class);
                        } catch (IOException e) {
                            log.error("Ошибка при десериализации TransactionEntity: {}", e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
            transactionRepository.saveAll(transactions);
            log.debug("Transaction consumer: записи сохранены");
        } finally {
            ack.acknowledge();
        }
    }
}
