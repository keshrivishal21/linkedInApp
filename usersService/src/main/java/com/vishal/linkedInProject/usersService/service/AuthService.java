package com.vishal.linkedInProject.usersService.service;

import com.vishal.linkedInProject.usersService.dto.LoginRequestDto;
import com.vishal.linkedInProject.usersService.dto.SignupRequestDto;
import com.vishal.linkedInProject.usersService.dto.UserDto;
import com.vishal.linkedInProject.usersService.entity.User;
import com.vishal.linkedInProject.usersService.event.UserCreatedEvent;
import com.vishal.linkedInProject.usersService.exception.BadRequestException;
import com.vishal.linkedInProject.usersService.repository.UserRepository;
import com.vishal.linkedInProject.usersService.utils.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JWTService jwtService;
    private final KafkaTemplate<Long,UserCreatedEvent>kafkaTemplate;

    public UserDto signup(SignupRequestDto signupRequestDto) {
        log.info("Signup a user with email: {}",signupRequestDto.getEmail());
        boolean exists = userRepository.existsByEmail(signupRequestDto.getEmail());
        if(exists){
            throw new BadRequestException("User already exists with email: " + signupRequestDto.getEmail());
        }
        User user = modelMapper.map(signupRequestDto, User.class);
        user.setPassword(BCrypt.hash(signupRequestDto.getPassword()));
        user = userRepository.save(user);

        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .userId(user.getId())
                .name(user.getName())
                .build();
        kafkaTemplate.send("user_created_topic",userCreatedEvent);
        return modelMapper.map(user, UserDto.class);
    }

    public String login(LoginRequestDto loginRequestDto) {
        log.info("Login attempt for user with email: {}",loginRequestDto.getEmail());
        User user = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(()
                -> new BadRequestException("Invalid credentials"));
        boolean passwordMatch = BCrypt.match(loginRequestDto.getPassword(), user.getPassword());
        if(!passwordMatch){
            throw new BadRequestException("Invalid credentials");
        }
        return jwtService.generateAccessToken(user);
    }
}
