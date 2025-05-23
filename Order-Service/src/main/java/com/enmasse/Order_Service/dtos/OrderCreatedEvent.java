package com.enmasse.Order_Service.dtos;

import com.enmasse.Order_Service.entity.OrderItem;

import java.util.List;

public record OrderCreatedEvent(
        String userId,
         List<OrderItemRequest> items,
         String sessionId
) { }
