package com.vishal.linkedInProject.connectionsService.service;

import com.vishal.linkedInProject.connectionsService.auth.AuthContextHolder;
import com.vishal.linkedInProject.connectionsService.entity.Person;
import com.vishal.linkedInProject.connectionsService.exception.BadRequestException;
import com.vishal.linkedInProject.connectionsService.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionsService {
    private final PersonRepository personRepository;

    public List<Person> getFirstDegreeConnectionsOfUser(Long userId){
        log.info("Fetching first degree connections for user with ID: {}", userId);
        return personRepository.getFirstDegreeConnections(userId);
    }

    public List<Person> getSecondDegreeConnectionsOfUser(Long userId){
        log.info("Fetching second degree connections for user with ID: {}", userId);
        return personRepository.getSecondDegreeConnections(userId);
    }


    public void sendConnectionRequest(Long receiverID) {
        Long senderId = AuthContextHolder.getCurrentUserId();
        log.info("User ID: {} is sending connection request to user ID: {}", senderId, receiverID);
        if(senderId.equals(receiverID)) throw new BadRequestException("Cannot send connection request to yourself");

        boolean alreadyRequested = personRepository.connectionRequestExists(senderId, receiverID);
        if(alreadyRequested) throw new BadRequestException("Connection request already sent to user");

        boolean alreadyConnected = personRepository.alreadyConnected(senderId, receiverID);
        if(alreadyConnected) throw new BadRequestException("Already connected users, cannot add connection request");

        personRepository.addConnectionRequest(senderId, receiverID);
        log.info("Connection request sent from user ID: {} to user ID: {}", senderId, receiverID);
    }

    public void acceptConnectionRequest(Long senderId){
        Long receiverId = AuthContextHolder.getCurrentUserId();

        if(senderId.equals(receiverId)) throw new BadRequestException("Cannot send connection request to yourself");

        boolean alreadyConnected = personRepository.alreadyConnected(senderId,receiverId);
        if (alreadyConnected) throw new BadRequestException("Already connected users, cannot accept connection request");

        boolean requestExists = personRepository.connectionRequestExists(receiverId,senderId);
        if (!requestExists) throw new BadRequestException("No connection request from user to accept");

        personRepository.acceptConnectionRequest(senderId,receiverId);
        log.info("Connection request from user ID: {} accepted by user ID: {}", senderId, receiverId);
    }

    public void rejectConnectionRequest(Long senderId){
        Long receiverId = AuthContextHolder.getCurrentUserId();

        if(senderId.equals(receiverId)) throw new BadRequestException("Cannot send connection request to yourself");

        boolean requestExists = personRepository.connectionRequestExists(receiverId,senderId);
        if (!requestExists) throw new BadRequestException("No connection request from user to reject");

        personRepository.rejectConnectionRequest(senderId,receiverId);
        log.info("Connection request from user ID: {} rejected by user ID: {}", senderId, receiverId);
    }
}
