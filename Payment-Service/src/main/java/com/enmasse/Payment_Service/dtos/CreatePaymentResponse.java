package com.enmasse.Payment_Service.dtos;

import lombok.Builder;


@Builder
public record CreatePaymentResponse (
     String sessionId,
     String sessionUrl
){}
