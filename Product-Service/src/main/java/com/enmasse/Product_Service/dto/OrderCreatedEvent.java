package com.enmasse.Product_Service.dto;

import java.util.List;

public record OrderCreatedEvent(
        Long userId,
         List<OrderItemRequest> items,
         String sessionId
) { }
