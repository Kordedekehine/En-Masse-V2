package com.enmasse.Notification_Service.controller;

import com.enmasse.Notification_Service.dtos.NotificationRequest;
import com.enmasse.Notification_Service.entity.Notification;
import com.enmasse.Notification_Service.exception.NotificationException;
import com.enmasse.Notification_Service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notify")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) throws NotificationException {
        notificationService.sendNotification(request);
        return ResponseEntity.ok("Notification sent successfully to " + request.toEmail());
    }


    @GetMapping("/getUserMails")
    public ResponseEntity<Page<Notification>> getUserNotifications(
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notification> notifications = notificationService.getNotificationsForUser(email, pageable);
        return ResponseEntity.ok(notifications);
    }
}
