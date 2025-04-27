package com.enmasse.Order_Service.dtos;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String productName,
        String productImageUrl,
        BigDecimal unitPrice,
        Integer quantity
){}
