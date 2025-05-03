package com.enmasse.Order_Service.config;

import com.enmasse.Order_Service.dtos.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendNotification(NotificationEvent event) {
        String topic = "notification-topic";
        kafkaTemplate.send(topic, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send message to topic: {}", topic, ex);
            } else {
                log.info("Message sent successfully to topic: {}", topic);
            }
        });
    }

}

