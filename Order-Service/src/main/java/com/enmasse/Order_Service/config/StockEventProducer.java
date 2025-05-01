package com.enmasse.Order_Service.config;

import com.enmasse.Order_Service.dtos.ProductStockDto;
import com.enmasse.Order_Service.dtos.ProductStockUpdateEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockEventProducer {

    private final KafkaTemplate<String, ProductStockUpdateEvent> kafkaTemplate;

    public StockEventProducer(KafkaTemplate<String, ProductStockUpdateEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendStockUpdateEvent(List<ProductStockDto> stockUpdates) {
        ProductStockUpdateEvent event = ProductStockUpdateEvent.builder()
                .stockUpdates(stockUpdates)
                .build();

        kafkaTemplate.send("product-stock-update-topic", event);
    }
}
