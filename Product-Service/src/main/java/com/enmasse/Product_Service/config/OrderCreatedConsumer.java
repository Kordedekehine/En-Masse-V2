package com.enmasse.Product_Service.config;

import com.enmasse.Product_Service.dto.OrderCreatedEvent;
import com.enmasse.Product_Service.dto.ProductStockDto;
import com.enmasse.Product_Service.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrderCreatedConsumer {

    private final ProductService productService;

    public OrderCreatedConsumer(ProductService productService) {
        this.productService = productService;
    }

    @KafkaListener(topics = "order-created-topic", groupId = "product-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: {}", event);
        String topic = "order-created-topic";
        productService.consumeOrderCreatedEvent(event);
    }
}
