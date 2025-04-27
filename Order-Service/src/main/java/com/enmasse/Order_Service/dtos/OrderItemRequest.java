package com.enmasse.Order_Service.dtos;

import java.math.BigDecimal;

public record OrderItemRequest(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal price
){}
