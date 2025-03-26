package com.project.bloghub.connections_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.send-connection-request-topic}")
    private String KAFKA_SEND_CONNECTION_REQUEST_TOPIC;

    @Value("${kafka.topic.accept-connection-request-topic}")
    private String KAFKA_ACCEPT_CONNECTION_REQUEST_TOPIC;

    @Bean
    public NewTopic sendConnectionRequestTopic(){
        return new NewTopic(KAFKA_SEND_CONNECTION_REQUEST_TOPIC, 3, (short) 1);
    }

    @Bean
    public NewTopic acceptConnectionRequestTopic(){
        return new NewTopic(KAFKA_ACCEPT_CONNECTION_REQUEST_TOPIC, 3, (short) 1);
    }
}
