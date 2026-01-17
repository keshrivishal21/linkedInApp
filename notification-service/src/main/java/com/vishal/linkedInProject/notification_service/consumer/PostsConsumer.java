package com.vishal.linkedInProject.notification_service.consumer;

import com.vishal.linkedInProject.notification_service.entity.Notification;
import com.vishal.linkedInProject.notification_service.service.NotificationService;
import com.vishal.linkedInProject.postsService.event.PostCreated;
import com.vishal.linkedInProject.postsService.event.PostLiked;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostsConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "post_created_topic")
    public void handlePostCreated(PostCreated postCreated){
        log.info("Received PostCreated event for postId: {} by userId: {}", postCreated.getPostId(), postCreated.getUserId());
        String message = String.format("New post created by user %d: %s", postCreated.getOwnerUserId(), postCreated.getContent());
        Notification notification = Notification.builder()
                .userId(postCreated.getUserId())
                .message(message)
                .build();
        notificationService.addNotification(notification);
    }

    @KafkaListener(topics = "post_liked_topic")
    public void handlePostLiked(PostLiked postLiked){
        log.info("Received PostLiked event for postId: {} liked by userId: {}", postLiked.getPostId(), postLiked.getLikedByUserId());
        String message = String.format("Your post %d was liked by user %d", postLiked.getPostId(), postLiked.getLikedByUserId());
        Notification notification = Notification.builder()
                .userId(postLiked.getOwnerUserId())
                .message(message)
                .build();
        notificationService.addNotification(notification);
    }
}
