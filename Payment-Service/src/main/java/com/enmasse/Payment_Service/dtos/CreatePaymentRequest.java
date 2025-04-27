package com.enmasse.Payment_Service.dtos;

import lombok.Builder;
import lombok.Data;

@Builder
public record CreatePaymentRequest(
     Long amount,
     Long quantity,
     String currency,
     String name,
     String successUrl,
     String cancelUrl
){}
