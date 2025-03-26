package com.project.bloghub.post_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.post-created-topic}")
    private String KAFKA_POST_CREATED_TOPIC;

    @Value("${kafka.topic.post-liked-topic}")
    private String KAFKA_POST_LIKED_TOPIC;

    @Bean
    public NewTopic postCreatedTopic(){
        return new NewTopic(KAFKA_POST_CREATED_TOPIC,3, (short) 1);
    }

    @Bean
    public NewTopic postLikedTopic(){
        return new NewTopic(KAFKA_POST_LIKED_TOPIC, 3, (short) 1);
    }
}
