package com.project.bloghub.notification_service.service;

import com.project.bloghub.notification_service.entity.Notification;
import com.project.bloghub.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
/*
 * Service class to handle logic related to sending notifications
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SendNotificationService {

    private final NotificationRepository notificationRepository;

    public void sendNotification(Long userId, String message){
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);

        notificationRepository.save(notification);

        log.info("Notification saved for user: {}", userId);
    }
}
