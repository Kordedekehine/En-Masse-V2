package com.enmasse.Notification_Service.config;

import com.enmasse.Notification_Service.dtos.NotificationRequest;
import com.enmasse.Notification_Service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class NotificationKafkaListener {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "send-notification", groupId = "notification-group")
    public void listen(NotificationRequest request) {
        notificationService.sendNotification(request);
    }
}
