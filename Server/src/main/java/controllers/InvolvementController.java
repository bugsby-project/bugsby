package controllers;

import dtos.InvolvementDto;
import exceptions.ProjectNotFoundException;
import exceptions.UserAlreadyInProjectException;
import exceptions.UserNotFoundException;
import exceptions.UserNotInProjectException;
import model.Involvement;
import model.Project;
import model.User;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.Service;
import utils.requests.AddParticipantRequest;

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
