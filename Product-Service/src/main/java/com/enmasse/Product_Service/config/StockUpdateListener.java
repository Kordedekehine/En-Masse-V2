package com.enmasse.Product_Service.config;

import com.enmasse.Product_Service.dto.OrderCreatedEvent;
import com.enmasse.Product_Service.dto.ProductStockDto;
import com.enmasse.Product_Service.dto.ProductStockUpdateEvent;
import com.enmasse.Product_Service.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StockUpdateListener {

    private final ProductService productService;

    public StockUpdateListener(ProductService productService) {
        this.productService = productService;
    }

    @KafkaListener(topics = "product-stock-update-topic", groupId = "product-group", containerFactory = "kafkaListenerContainerFactory")
    public void updateProductStockEvent(ProductStockUpdateEvent productsOrdered) {
        log.info("Received UpdateProductStock: {}", productsOrdered);
        String topic = "product-stock-update-topic";
        productService.updateProductStock(productsOrdered.stockUpdates());
    }
}
