package com.enmasse.Order_Service.dtos;

import lombok.Builder;


@Builder
public record CapturePaymentResponse(
     String sessionId,
     String sessionStatus,
     String paymentStatus
) { }
