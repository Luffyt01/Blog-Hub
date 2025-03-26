package com.project.bloghub.notification_service.consumer;

import com.project.bloghub.notification_service.clients.ConnectionsClient;
import com.project.bloghub.notification_service.dto.PersonDto;
import com.project.bloghub.notification_service.repository.NotificationRepository;
import com.project.bloghub.notification_service.service.SendNotificationService;
import com.project.bloghub.post_service.event.PostCreatedEvent;
import com.project.bloghub.post_service.event.PostLikedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostsServiceConsumer {

    private final ConnectionsClient connectionsClient;
    private final NotificationRepository notificationRepository;
    private final SendNotificationService sendNotificationService;

    @KafkaListener(topics = "post-created-topic")
    public void handlePostCreated(PostCreatedEvent postCreatedEvent){
        log.info("Sending notification: handlePostCreated");
        List<PersonDto> connections = connectionsClient.getFirstConnections(postCreatedEvent.getCreatorId());

        for (PersonDto connection: connections){
            sendNotificationService.sendNotification(connection.getUserId(), "Your connection "+postCreatedEvent.getCreatorId()
                    +" has created a post, Check it out.");
        }

    }

    @KafkaListener(topics = "post-liked-topic")
    public void handlePostLiked(PostLikedEvent postLikedEvent){
        log.info("Sending notification: handlePostLiked: {}", postLikedEvent);

        String message = String.format("Your post, %d has been liked by %d", postLikedEvent.getPostId(), postLikedEvent.getLikedByUserId());

        sendNotificationService.sendNotification(postLikedEvent.getCreatorId(), message);
    }



}
