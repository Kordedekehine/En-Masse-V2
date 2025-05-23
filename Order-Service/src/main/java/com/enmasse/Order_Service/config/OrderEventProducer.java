package com.enmasse.Order_Service.config;

import com.enmasse.Order_Service.dtos.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        String topic = "order-events";
        kafkaTemplate.send(topic, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send message to topic: {}", topic, ex);
            } else {
                log.info("Message sent successfully to topic: {}", topic);
            }
        });
    }
}
