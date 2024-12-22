package main.java.ru.t1.java.starter.track.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTrackProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, Object message) {
        try {
            kafkaTemplate.send(topic, message.toString()).get();
            log.info("Message sent to Kafka topic {}", topic);
        } catch (Exception e) {
            log.error("Failed to send message: {}", e.getMessage());
        }
    }
}
