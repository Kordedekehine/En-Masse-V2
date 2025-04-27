package com.enmasse.Payment_Service.dtos;

import java.util.List;

public record OrderCreatedEvent(
        Long userId,
         List<OrderItemRequest> items,
         String sessionId
) { }
