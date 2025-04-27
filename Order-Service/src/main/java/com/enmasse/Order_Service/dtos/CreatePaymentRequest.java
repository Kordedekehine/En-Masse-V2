package com.enmasse.Order_Service.dtos;

import lombok.Builder;

@Builder
public record CreatePaymentRequest(
     Long amount,
     Long quantity,
     String currency,
     String name,
     String successUrl,
     String cancelUrl
){}
