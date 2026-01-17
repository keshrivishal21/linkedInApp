package com.vishal.linkedInProject.connectionsService.consumer;

import com.vishal.linkedInProject.connectionsService.service.PersonService;
import com.vishal.linkedInProject.usersService.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserConsumerService {
    private final PersonService personService;

    @KafkaListener(topics = "user_created_topic", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePersonCreated(UserCreatedEvent userCreatedEvent){
        log.info("Received UserCreatedEvent for userId: {} with name: {}", userCreatedEvent.getUserId(), userCreatedEvent.getName());
        personService.createPerson(userCreatedEvent.getUserId(), userCreatedEvent.getName());
    }
}
