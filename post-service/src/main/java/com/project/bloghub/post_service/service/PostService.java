package com.project.bloghub.post_service.service;

import com.project.bloghub.post_service.auth.UserContextHolder;
import com.project.bloghub.post_service.clients.ConnectionsClient;
import com.project.bloghub.post_service.dto.PersonDto;
import com.project.bloghub.post_service.dto.PostCreateRequestDto;
import com.project.bloghub.post_service.dto.PostDto;
import com.project.bloghub.post_service.entity.Post;
import com.project.bloghub.post_service.event.PostCreatedEvent;
import com.project.bloghub.post_service.exception.ResourceNotFoundException;
import com.project.bloghub.post_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    @Value("${kafka.topic.post-created-topic}")
    private String KAFKA_POST_CREATED_TOPIC;

    private final ModelMapper modelMapper;
    private final PostRepository postRepository;
    private final ConnectionsClient connectionsClient;

    private final KafkaTemplate<Long, PostCreatedEvent> kafkaTemplate;


    public PostDto createPost(PostCreateRequestDto postCreateRequestDto) {
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("Creating post for user with id: {}",userId);
        Post post = modelMapper.map(postCreateRequestDto, Post.class);
        post.setUserId(userId);

        Post savedPost = postRepository.save(post);

        PostCreatedEvent postCreatedEvent = PostCreatedEvent.builder()
                        .postId(savedPost.getId())
                        .creatorId(userId)
                        .content(savedPost.getContent())
                        .build();

        kafkaTemplate.send(KAFKA_POST_CREATED_TOPIC, postCreatedEvent);

        return modelMapper.map(savedPost, PostDto.class);
    }

    public PostDto getPostById(Long postId) {
        log.info("Fetching post with id: {}", postId);

        Post post =  postRepository.findById(postId).orElseThrow(() ->
                new ResourceNotFoundException("Post not found with id: "+postId));

        return modelMapper.map(post, PostDto.class);
    }

    public List<PostDto> getAllPostOfUser() {
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("Fetching all the post for user with id: {}",userId);

        List<Post> posts = postRepository.findPostByUserId(userId);

        return posts.stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .toList();
    }
}
