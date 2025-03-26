package com.project.bloghub.notification_service.consumer;

import com.project.bloghub.connections_service.event.AcceptConnectionRequestEvent;
import com.project.bloghub.connections_service.event.SendConnectionRequestEvent;
import com.project.bloghub.notification_service.service.SendNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionServiceConsumer {

    private final SendNotificationService sendNotificationService;

    @KafkaListener(topics = "send-connection-request-topic")
    public void handleSendConnectionRequest(SendConnectionRequestEvent sendConnectionRequestEvent){
        log.info("handle connections: handleSendConnectionRequest: {}", sendConnectionRequestEvent);
        String message = "You have received a connection request from user with id: "+sendConnectionRequestEvent.getSenderId();

        sendNotificationService.sendNotification(sendConnectionRequestEvent.getReceiverId(), message);
    }

    @KafkaListener(topics = "accept-connection-request-topic")
    public void handleAcceptConnectionRequestEvent(AcceptConnectionRequestEvent acceptConnectionRequestEvent){
        log.info("handle connections: handleAcceptConnectionRequestEvent: {}", acceptConnectionRequestEvent);

        String message = "Your connection request has been accepted by the user with id: "+acceptConnectionRequestEvent.getReceiverId();
        sendNotificationService.sendNotification(acceptConnectionRequestEvent.getSenderId(), message);
    }
}
