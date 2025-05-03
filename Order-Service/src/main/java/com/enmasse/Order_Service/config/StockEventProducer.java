package com.enmasse.Order_Service.config;

import com.enmasse.Order_Service.dtos.ProductStockDto;
import com.enmasse.Order_Service.dtos.ProductStockUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StockEventProducer {

    private final KafkaTemplate<String, ProductStockUpdateEvent> kafkaTemplate;

    public StockEventProducer(KafkaTemplate<String, ProductStockUpdateEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendStockUpdateEvent(List<ProductStockDto> stockUpdates) {
        ProductStockUpdateEvent event = ProductStockUpdateEvent.builder()
                .stockUpdates(stockUpdates)
                .build();
        String topic = "product-stock-update-topic";

        kafkaTemplate.send(topic, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send message to topic: {}", topic, ex);
            } else {
                log.info("Message sent successfully to topic: {}", topic);
            }
        });
    }

}
