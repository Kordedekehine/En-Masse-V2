package com.enmasse.Order_Service.dtos;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreatePaymentRequest(
     BigDecimal amount,
     Long quantity,
     String currency,
     String name,
     String successUrl,
     String cancelUrl
){}
