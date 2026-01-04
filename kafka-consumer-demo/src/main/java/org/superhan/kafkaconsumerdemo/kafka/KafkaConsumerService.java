package org.superhan.kafkaconsumerdemo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {

    public static final String TOPIC = "test-topic";
    public static final String GROUP_ID = "kafka-consumer-demo-group";

    @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
    public void consume(ConsumerRecord<String, String> record) {
        log.info("Received message - Topic: {}, Partition: {}, Offset: {}, Key: {}, Value: {}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                record.value());
    }
}
