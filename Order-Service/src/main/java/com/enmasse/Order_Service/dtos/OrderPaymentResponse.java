package com.enmasse.Order_Service.dtos;

import java.math.BigDecimal;
import java.util.Map;

public record OrderPaymentResponse(
        Long orderId,
        String orderNumber,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        Map<String, String> payerDetails,
        PaymentResult resultStatus,
        String resultMessage
) {}
