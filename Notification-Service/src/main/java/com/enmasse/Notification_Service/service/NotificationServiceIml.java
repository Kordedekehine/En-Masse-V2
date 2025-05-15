package com.enmasse.Notification_Service.service;

import com.enmasse.Notification_Service.dtos.NotificationRequest;
import com.enmasse.Notification_Service.entity.Notification;
import com.enmasse.Notification_Service.exception.NotificationException;
import com.enmasse.Notification_Service.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceIml implements NotificationService {


    private final JavaMailSender mailSender;

    @Autowired
    private NotificationRepository notificationRepository;

    public NotificationServiceIml(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendNotification(NotificationRequest request) throws NotificationException {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(request.toEmail());
            helper.setSubject(request.subject());
            helper.setText(request.message(), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new NotificationException("Failed to send email to " + request.toEmail());
        }

       // After successful email sending, save the notification to the database
        Notification notification = Notification.builder()
                .toEmail(request.toEmail())
                .title(request.subject())
                .message(request.message())
                .status("SENT")
                .createdAt(LocalDateTime.now())
                .sentAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

    }

    @Override
    public Page<Notification> getNotificationsForUser(String email, Pageable pageable) {
        return notificationRepository.findByToEmailOrderByCreatedAtDesc(email, pageable);
    }

}
