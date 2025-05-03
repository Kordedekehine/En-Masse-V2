package com.enmasse.Notification_Service.repository;

import com.enmasse.Notification_Service.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByToEmailOrderByCreatedAtDesc(String toEmail, Pageable pageable);

}
