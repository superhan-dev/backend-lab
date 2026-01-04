package org.superhan.otlpdemo.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
