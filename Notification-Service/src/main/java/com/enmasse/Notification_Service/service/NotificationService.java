package com.enmasse.Notification_Service.service;

import com.enmasse.Notification_Service.dtos.NotificationRequest;
import com.enmasse.Notification_Service.entity.Notification;
import com.enmasse.Notification_Service.exception.NotificationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    void sendNotification(NotificationRequest request) throws NotificationException;

    Page<Notification> getNotificationsForUser(String email, Pageable pageable) throws NotificationException;

}
