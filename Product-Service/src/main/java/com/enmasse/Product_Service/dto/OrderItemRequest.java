package com.enmasse.Product_Service.dto;

import java.math.BigDecimal;

public record OrderItemRequest(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal price
){}
