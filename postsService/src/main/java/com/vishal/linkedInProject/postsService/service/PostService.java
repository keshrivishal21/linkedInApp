package com.vishal.linkedInProject.postsService.service;

import com.vishal.linkedInProject.postsService.auth.AuthContextHolder;
import com.vishal.linkedInProject.postsService.client.ConnectionsServiceClient;
import com.vishal.linkedInProject.postsService.client.UploaderServiceClient;
import com.vishal.linkedInProject.postsService.dto.PersonDto;
import com.vishal.linkedInProject.postsService.dto.PostCreateRequestDto;
import com.vishal.linkedInProject.postsService.dto.PostDto;
import com.vishal.linkedInProject.postsService.entity.Post;
import com.vishal.linkedInProject.postsService.event.PostCreated;
import com.vishal.linkedInProject.postsService.exception.ResourceNotFoundException;
import com.vishal.linkedInProject.postsService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final ConnectionsServiceClient connectionsServiceClient;
    private final KafkaTemplate<Long, PostCreated>postCreatedKafkaTemplate;
    private final UploaderServiceClient uploaderServiceClient;


    public PostDto createPost(PostCreateRequestDto postCreateRequestDto,MultipartFile file) {
        Long userId = AuthContextHolder.getCurrentUserId();
        log.info("creating post for user with user id: {}",userId);
        ResponseEntity<String> imageUrl = uploaderServiceClient.uploadFile(file);
        Post post = modelMapper.map(postCreateRequestDto,Post.class);
        post.setUserId(userId);
        post.setImageUrl(imageUrl.getBody());
        post = postRepository.save(post);

        List<PersonDto>personDto = connectionsServiceClient.getFirstDegreeConnections(userId);
        for(PersonDto person: personDto){
            PostCreated postCreatedEvent = PostCreated.builder()
                    .postId(post.getId())
                    .userId(person.getUserId())
                    .content(post.getContent())
                    .ownerUserId(userId)
                    .build();
            log.info("Sending post created event to Kafka for user id: {} and post id: {}",person.getId(),post.getId());
            postCreatedKafkaTemplate.send("post_created_topic",postCreatedEvent);
        }

        return modelMapper.map(post,PostDto.class);
    }

    public List<PostDto> getAllPostsOfUser(Long userId) {
        List<Post> postList = postRepository.findByUserId(userId);
        return postList
                .stream()
                .map((element)->modelMapper.map(element,PostDto.class))
                .collect(Collectors.toList());
    }

    public PostDto getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException("Post not found " + "with id: "+ postId));
        return modelMapper.map(post,PostDto.class);
    }
}
