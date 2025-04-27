package com.enmasse.Order_Service.dtos;

import java.math.BigDecimal;
import java.util.Map;


public record OrderPaymentRequest(
        Long orderId,
        String orderNumber,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        Map<String, String> payerDetails
) {}
