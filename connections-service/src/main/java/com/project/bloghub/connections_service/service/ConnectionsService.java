package com.project.bloghub.connections_service.service;

import com.project.bloghub.connections_service.auth.UserContextHolder;
import com.project.bloghub.connections_service.entity.Person;
import com.project.bloghub.connections_service.event.AcceptConnectionRequestEvent;
import com.project.bloghub.connections_service.event.SendConnectionRequestEvent;
import com.project.bloghub.connections_service.exception.BadRequestException;
import com.project.bloghub.connections_service.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionsService {

    @Value("${kafka.topic.send-connection-request-topic}")
    private String KAFKA_SEND_CONNECTION_REQUEST_TOPIC;

    @Value("${kafka.topic.accept-connection-request-topic}")
    private String KAFKA_ACCEPT_CONNECTION_REQUEST_TOPIC;

    private final PersonRepository personRepository;
    private final KafkaTemplate<Long, SendConnectionRequestEvent> sendRequestKafkaTemplate;
    private final KafkaTemplate<Long, AcceptConnectionRequestEvent> acceptRequestKafkaTemplate;


    public Boolean sendConnectionRequest(Long receiverId) {
        Long senderId = UserContextHolder.getCurrentUserId();
        log.info("Sending connection request, sender {} receiver: {}",senderId,receiverId);

        if (senderId.equals(receiverId)){
            throw new BadRequestException("Both sender and receiver are the same");
        }

        boolean alreadySentRequest = personRepository.connectionRequestExists(senderId, receiverId);
        if (alreadySentRequest){
            throw new BadRequestException("Connection request already exists, cannot send again");
        }

        boolean alreadyConnected = personRepository.alreadyConnected(senderId, receiverId);
        if (alreadyConnected){
            throw new BadRequestException("Already connected users, cannot add connection request");
        }

        log.info("Successfully send the connection request");
        personRepository.addConnectionRequest(senderId, receiverId);


        SendConnectionRequestEvent sendConnectionRequestEvent = SendConnectionRequestEvent.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .build();

        sendRequestKafkaTemplate.send(KAFKA_SEND_CONNECTION_REQUEST_TOPIC, sendConnectionRequestEvent);

        return true;
    }

    public List<Person> getFirstDegreeConnections(){
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("Getting first degree connections for user with id: {}", userId);

        return personRepository.getFirstDegreeConnections(userId);
    }

    public Boolean acceptConnectionRequest(Long senderId) {
        Long receiverId = UserContextHolder.getCurrentUserId();

        boolean connectionRequestExists = personRepository.connectionRequestExists(receiverId, senderId);
        if (!connectionRequestExists){
            throw new BadRequestException("No connection request exists to accept");
        }

        personRepository.acceptConnectionRequest(receiverId, senderId);

        log.info("Successfully accepted the connection request, sender: {}, receiver: {}",senderId, receiverId);

        AcceptConnectionRequestEvent acceptConnectionRequestEvent = AcceptConnectionRequestEvent.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .build();

        acceptRequestKafkaTemplate.send(KAFKA_ACCEPT_CONNECTION_REQUEST_TOPIC, acceptConnectionRequestEvent);

        return true;
    }


    public Boolean rejectConnectionRequest(Long senderId) {
        Long receiverId = UserContextHolder.getCurrentUserId();

        boolean connectionRequestExists = personRepository.connectionRequestExists(receiverId, senderId);
        if (!connectionRequestExists){
            throw new BadRequestException("No connection request exists, cannot delete");
        }

        personRepository.rejectConnectionRequest(receiverId, senderId);

        return true;
    }
}
