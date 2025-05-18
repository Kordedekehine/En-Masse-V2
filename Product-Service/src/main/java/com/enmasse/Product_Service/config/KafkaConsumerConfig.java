package com.enmasse.Product_Service.config;

import com.enmasse.Product_Service.dto.OrderCreatedEvent;
import com.enmasse.Product_Service.dto.ProductStockDto;
import com.enmasse.Product_Service.dto.ProductStockUpdateEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductStockUpdateEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, ProductStockUpdateEvent> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, ProductStockUpdateEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler();
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }


}
