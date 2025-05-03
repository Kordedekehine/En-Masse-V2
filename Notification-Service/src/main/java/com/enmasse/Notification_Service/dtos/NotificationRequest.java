package com.enmasse.Notification_Service.dtos;

import lombok.Builder;

@Builder
public record NotificationRequest (
         String toEmail,
         String subject,
         String message
){ }
