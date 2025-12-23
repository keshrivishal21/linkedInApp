package com.vishal.linkedInProject.connectionsService.controller;

import com.vishal.linkedInProject.connectionsService.entity.Person;
import com.vishal.linkedInProject.connectionsService.service.ConnectionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
public class ConnectionsController {

    private final ConnectionsService connectionsService;

    @GetMapping("/{userId}/first-degree")
    public ResponseEntity<List<Person>>getFirstDegreeConnections(@PathVariable Long userId){
        List<Person> firstDegreeConnections = connectionsService.getFirstDegreeConnectionsOfUser(userId);
        return ResponseEntity.ok(firstDegreeConnections);
    }
}
