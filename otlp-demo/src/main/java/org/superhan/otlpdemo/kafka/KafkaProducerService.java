package org.superhan.otlpdemo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public static final String TOPIC = "test-topic";

    public void sendMessage(String message) {
        log.info("Sending message: {}", message);
        kafkaTemplate.send(TOPIC, message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Message sent successfully: {} with offset: {}",
                                message, result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send message: {}", message, ex);
                    }
                });
    }

    public void sendMessageWithKey(String key, String message) {
        log.info("Sending message with key: {} message: {}", key, message);
        kafkaTemplate.send(TOPIC, key, message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Message sent successfully with key: {} offset: {}",
                                key, result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send message with key: {}", key, ex);
                    }
                });
    }
}
