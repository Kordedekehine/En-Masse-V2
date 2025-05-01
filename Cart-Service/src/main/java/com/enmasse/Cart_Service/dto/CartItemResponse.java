package com.enmasse.Cart_Service.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        String productId,
        String productName,
        String productImageUrl,
        BigDecimal unitPrice,
        Integer quantity) {
}
