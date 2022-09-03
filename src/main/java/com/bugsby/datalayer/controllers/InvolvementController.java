package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.controllers.dtos.InvolvementDto;
import com.bugsby.datalayer.service.exceptions.ProjectNotFoundException;
import com.bugsby.datalayer.service.exceptions.UserAlreadyInProjectException;
import com.bugsby.datalayer.service.exceptions.UserNotFoundException;
import com.bugsby.datalayer.service.exceptions.UserNotInProjectException;
import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.service.Service;
import com.bugsby.datalayer.controllers.utils.requests.AddParticipantRequest;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping(value = "/involvements")
public class InvolvementController {
    private final Service service;

    {
        var context = new ClassPathXmlApplicationContext("classpath:spring.xml");
        service = context.getBean(Service.class);
    }

    @GetMapping
    public ResponseEntity<?> getInvolvementsByUsername(@RequestParam(value = "username") String username) {
        try {
            Set<Involvement> involvements = service.getInvolvementsByUsername(username);
            Set<InvolvementDto> result = involvements
                    .stream()
                    .map(InvolvementDto::from)
                    .collect(Collectors.toSet());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> addParticipant(@RequestBody AddParticipantRequest request) {
        try {
            Project project = new Project(request.getProjectId());
            User participant = new User(request.getUsername());
            User requester = new User(request.getRequesterId());
            Involvement involvement = new Involvement(request.getRole(), participant, project);

            Involvement result = service.addParticipant(involvement, requester);
            if (result == null) {  // operation has failed
                return new ResponseEntity<>("Failed to add participant", HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(InvolvementDto.from(result), HttpStatus.CREATED);
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
