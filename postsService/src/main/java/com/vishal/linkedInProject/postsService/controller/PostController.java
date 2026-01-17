package com.vishal.linkedInProject.postsService.controller;

import com.vishal.linkedInProject.postsService.auth.AuthContextHolder;
import com.vishal.linkedInProject.postsService.dto.PostCreateRequestDto;
import com.vishal.linkedInProject.postsService.dto.PostDto;
import com.vishal.linkedInProject.postsService.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/core")
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDto> createPost(@RequestPart("post") PostCreateRequestDto postCreateRequestDto,
                                              @RequestPart("file") MultipartFile file){
        Long userId = AuthContextHolder.getCurrentUserId();
        PostDto postDto = postService.createPost(postCreateRequestDto, file);
        return new ResponseEntity<>(postDto, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto>getPost(@PathVariable Long postId){
        Long userId = AuthContextHolder.getCurrentUserId();
        log.info("User with ID: {} is fetching the post with ID: {}", userId, postId);
        PostDto postDto = postService.getPostById(postId);
        return ResponseEntity.ok(postDto);
    }

    @GetMapping("/users/{userId}/allPosts")
    public ResponseEntity<List<PostDto>>getAllPostsOfUser(@PathVariable Long userId){
        List<PostDto> posts = postService.getAllPostsOfUser(userId);
        return ResponseEntity.ok(posts);
    }

}
