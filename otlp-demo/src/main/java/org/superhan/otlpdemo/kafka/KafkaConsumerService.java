package org.superhan.otlpdemo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {

    @KafkaListener(topics = KafkaProducerService.TOPIC, groupId = "otlp-demo-group")
    public void consume(String message) {
        log.info("Received message: {}", message);
    }
}
