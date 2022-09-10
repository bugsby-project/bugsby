package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.controllers.dtos.InvolvementDto;
import com.bugsby.datalayer.controllers.dtos.requests.AddParticipantRequest;
import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.service.Service;
import com.bugsby.datalayer.service.exceptions.ProjectNotFoundException;
import com.bugsby.datalayer.service.exceptions.UserAlreadyInProjectException;
import com.bugsby.datalayer.service.exceptions.UserNotFoundException;
import com.bugsby.datalayer.service.exceptions.UserNotInProjectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping(value = "/involvements")
public class InvolvementController {
    @Autowired
    private Service service;
    @Autowired
    private Function<Involvement, InvolvementDto> involvementMapper;

    @GetMapping
    public ResponseEntity<?> getInvolvementsByUsername(@RequestParam(value = "username") String username) {
        try {
            Set<Involvement> involvements = service.getInvolvementsByUsername(username);
            Set<InvolvementDto> result = involvements
                    .stream()
                    .map(involvementMapper)
                    .collect(Collectors.toSet());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> addParticipant(@RequestBody AddParticipantRequest request) {
        try {
            Project project = new Project(request.projectId());
            User participant = new User(request.username());
            User requester = new User(request.requesterId());
            Involvement involvement = new Involvement(request.role(), participant, project);

            Involvement result = service.addParticipant(involvement, requester);
            if (result == null) {  // operation has failed
                return new ResponseEntity<>("Failed to add participant", HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(involvementMapper.apply(result), HttpStatus.CREATED);
            }
        } catch (UserNotFoundException | ProjectNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UserNotInProjectException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (UserAlreadyInProjectException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
