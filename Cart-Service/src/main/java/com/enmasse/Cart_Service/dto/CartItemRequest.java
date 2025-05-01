package com.enmasse.Cart_Service.dto;

import java.math.BigDecimal;

public record CartItemRequest (
        String productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity
) { }
