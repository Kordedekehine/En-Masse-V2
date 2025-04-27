package com.enmasse.Order_Service.dtos;

import lombok.Builder;


@Builder
public record CreatePaymentResponse(
     String sessionId,
     String sessionUrl
){}
