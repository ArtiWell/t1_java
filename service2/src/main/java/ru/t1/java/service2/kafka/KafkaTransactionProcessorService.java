package ru.t1.java.service2.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.service2.dto.TransactionMessage;
import ru.t1.java.service2.dto.TransactionResultMessage;
import ru.t1.java.service2.emums.TransactionStatus;
import ru.t1.java.service2.entity.TransactionEntity;
import ru.t1.java.service2.repository.TransactionRepository;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaTransactionProcessorService {

    private final KafkaTemplate<String, TransactionResultMessage> kafkaTemplate;
    private final TransactionRepository transactionRepository;

    @Value("${transaction.processing.time-window-millis}")
    private long timeWindowMillis;

    @Value("${transaction.processing.threshold}")
    private int transactionThreshold;

    public void processTransaction(TransactionMessage transactionMessage) {
        if (transactionMessage.amount() > transactionMessage.balance()) {
            sendTransactionResult(transactionMessage, TransactionStatus.REJECTED);
            return;
        }


        Instant windowStartTime = Instant.now().minusMillis(timeWindowMillis);

        List<TransactionEntity> recentTransactions = transactionRepository
                .findRecentTransactions(transactionMessage.clientId(), transactionMessage.accountId(), windowStartTime);

        if (recentTransactions.size() >= transactionThreshold) {
            recentTransactions.forEach(tx -> {
                tx.setStatus(TransactionStatus.BLOCKED);
                transactionRepository.save(tx);
            });
            sendTransactionResult(transactionMessage, TransactionStatus.BLOCKED);
        } else {
            sendTransactionResult(transactionMessage, TransactionStatus.ACCEPTED);
        }
    }


    private void sendTransactionResult(TransactionMessage transactionMessage, TransactionStatus status) {
        TransactionResultMessage resultMessage = new TransactionResultMessage(
                transactionMessage.clientId(),
                transactionMessage.accountId(),
                transactionMessage.transactionId(),
                status.name()
        );
        kafkaTemplate.send("t1_demo_transaction_result", resultMessage);
    }
}
