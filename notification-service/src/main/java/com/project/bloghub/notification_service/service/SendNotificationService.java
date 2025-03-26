package com.project.bloghub.notification_service.service;

import com.project.bloghub.notification_service.entity.Notification;
import com.project.bloghub.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendNotificationService {

    private final NotificationRepository notificationRepository;

    public void sendNotification(Long userId, String message){
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUserId(userId);

        notificationRepository.save(notification);
    }
}
