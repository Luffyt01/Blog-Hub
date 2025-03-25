package com.project.bloghub.post_service.controller;

import com.project.bloghub.post_service.auth.UserContextHolder;
import com.project.bloghub.post_service.dto.PostCreateRequestDto;
import com.project.bloghub.post_service.dto.PostDto;
import com.project.bloghub.post_service.entity.Post;
import com.project.bloghub.post_service.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    @PostMapping("/createPost")
    public ResponseEntity<PostDto> createPost(@RequestBody PostCreateRequestDto postCreateRequestDto){
        PostDto createdPost = postService.createPost(postCreateRequestDto);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long postId){
        PostDto postDto = postService.getPostById(postId);
        return ResponseEntity.ok(postDto);
    }


    @GetMapping("/users/allPosts")
    public ResponseEntity<List<PostDto>> getALlPostsOfUser(){
        List<PostDto> posts = postService.getAllPostOfUser();
        return ResponseEntity.ok(posts);
    }

}
