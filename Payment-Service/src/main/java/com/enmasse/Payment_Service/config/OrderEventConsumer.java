package com.enmasse.Payment_Service.config;

import com.enmasse.Payment_Service.dtos.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderEventConsumer {

    @KafkaListener(topics = "order-created-topic", groupId = "payment-group")
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: {}", event);

        // TODO: Implement your logic here, e.g:
        // - Validate payment session
        // - Update payment status
        // - Trigger further processing if needed
    }
}
