package com.vishal.linkedInProject.usersService.event;

import lombok.Builder;
import lombok.Data;

@Data
public class UserCreatedEvent {
    private Long userId;
    private String name;
}
