package com.project.bloghub.post_service.service;

import com.project.bloghub.post_service.entity.PostLike;
import com.project.bloghub.post_service.exception.BadRequestException;
import com.project.bloghub.post_service.exception.ResourceNotFoundException;
import com.project.bloghub.post_service.repository.PostLikeRepository;
import com.project.bloghub.post_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    public void likePost(Long postId, Long userId){
        log.info("Attempting to like the post with id:{}",postId);
        boolean exists = postRepository.existsById(postId);

        if (!exists) throw new ResourceNotFoundException("Post not found with id: "+postId);

        boolean alreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);

        if (alreadyLiked) throw new BadRequestException("Cannot like the same post again");

        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);

        postLikeRepository.save(postLike);
        log.info("Post with id: {} liked successfully", postId );
    }


    public void unlikePost(Long postId, long userId) {
        log.info("Attempting to unlike the post with id:{}",postId);
        boolean exists = postRepository.existsById(postId);

        if (!exists) throw new ResourceNotFoundException("Post not found with id: "+postId);

        boolean alreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);

        if (!alreadyLiked) throw new BadRequestException("Cannot unlike the same post which is not liked");

        postLikeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("Post with id: {} unliked successfully", postId );

    }
}
