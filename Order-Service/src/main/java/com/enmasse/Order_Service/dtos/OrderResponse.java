package com.enmasse.Order_Service.dtos;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderResponse(
        Long id,
        String userId,
        String orderNumber,
        List<OrderItemResponse> items,
        BigDecimal totalCost,
        String deliveryAddress,
        String status,
        String created,
        String sessionId,
        String sessionUrl
){}
