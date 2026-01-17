package com.vishal.linkedInProject.connectionsService.controller;

import com.vishal.linkedInProject.connectionsService.entity.Person;
import com.vishal.linkedInProject.connectionsService.service.ConnectionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
public class ConnectionsController {

    private final ConnectionsService connectionsService;

    @GetMapping("/{userId}/first-degree")
    public ResponseEntity<List<Person>>getFirstDegreeConnections(@PathVariable Long userId){
        log.info("Fetching first-degree connections for user with ID: {}", userId);
        List<Person> firstDegreeConnections = connectionsService.getFirstDegreeConnectionsOfUser(userId);
        return ResponseEntity.ok(firstDegreeConnections);
    }

    @PostMapping("/request/{userId}")
    public ResponseEntity<Void> sendConnectionRequest(@PathVariable Long userId){
        log.info("Sending connection request to user with ID: {}", userId);
        connectionsService.sendConnectionRequest(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{userId}")
    public ResponseEntity<Void> acceptConnectionRequest(@PathVariable Long userId){
        log.info("accepting connection request from user with ID: {}", userId);
        connectionsService.acceptConnectionRequest(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{userId}")
    public ResponseEntity<Void> rejectConnectionRequest(@PathVariable Long userId){
        log.info("Rejecting connection request from user with ID: {}", userId);
        connectionsService.rejectConnectionRequest(userId);
        return ResponseEntity.ok().build();
    }
}
