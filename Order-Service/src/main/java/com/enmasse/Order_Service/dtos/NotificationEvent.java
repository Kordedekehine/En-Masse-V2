package com.enmasse.Order_Service.dtos;

import lombok.Builder;

@Builder
public record NotificationEvent(
        String recipientEmail,
        String message,
        String eventType
) {
}
