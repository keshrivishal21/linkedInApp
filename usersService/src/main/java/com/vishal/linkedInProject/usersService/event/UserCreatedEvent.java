package com.vishal.linkedInProject.usersService.event;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreatedEvent {
    private Long userId;
    private String name;
}
