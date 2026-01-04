package org.superhan.otlpdemo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class KafkaController {

    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestParam String message) {
        kafkaProducerService.sendMessage(message);
        return ResponseEntity.ok("Message sent: " + message);
    }

    @PostMapping("/send-with-key")
    public ResponseEntity<String> sendMessageWithKey(
            @RequestParam String key,
            @RequestParam String message) {
        kafkaProducerService.sendMessageWithKey(key, message);
        return ResponseEntity.ok("Message sent with key: " + key);
    }

    @PostMapping("/send-bulk")
    public ResponseEntity<String> sendBulkMessages(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(defaultValue = "0") int delayMs) {

        CompletableFuture.runAsync(() -> {
            for (int i = 1; i <= count; i++) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
                String message = String.format("[%s] Bulk message %d/%d", timestamp, i, count);
                kafkaProducerService.sendMessage(message);

                if (delayMs > 0 && i < count) {
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            log.info("Bulk send completed: {} messages", count);
        });

        return ResponseEntity.ok(String.format("Sending %d messages with %dms delay", count, delayMs));
    }

    @PostMapping("/send-continuous")
    public ResponseEntity<String> sendContinuousMessages(
            @RequestParam(defaultValue = "60") int durationSeconds,
            @RequestParam(defaultValue = "100") int intervalMs) {

        CompletableFuture.runAsync(() -> {
            long endTime = System.currentTimeMillis() + (durationSeconds * 1000L);
            int count = 0;

            while (System.currentTimeMillis() < endTime) {
                count++;
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
                String message = String.format("[%s] Continuous msg #%d", timestamp, count);
                kafkaProducerService.sendMessage(message);

                try {
                    Thread.sleep(intervalMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            log.info("Continuous send completed: {} messages in {} seconds", count, durationSeconds);
        });

        return ResponseEntity.ok(String.format("Sending messages for %d seconds (interval: %dms)", durationSeconds, intervalMs));
    }

    @PostMapping("/send-topics")
    public ResponseEntity<String> sendToMultipleTopics(
            @RequestParam(defaultValue = "5") int messagesPerTopic) {

        String[] topics = {"orders", "payments", "notifications", "analytics"};

        for (String topic : topics) {
            for (int i = 1; i <= messagesPerTopic; i++) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
                String message = String.format("[%s] %s event #%d", timestamp, topic, i);
                kafkaProducerService.sendMessageWithKey(topic, message);
            }
        }

        return ResponseEntity.ok(String.format("Sent %d messages to %d topics", messagesPerTopic * topics.length, topics.length));
    }
}
