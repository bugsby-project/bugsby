package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.controllers.dtos.IssueDto;
import com.bugsby.datalayer.service.exceptions.AiServiceException;
import com.bugsby.datalayer.service.exceptions.IssueNotFoundException;
import com.bugsby.datalayer.service.exceptions.ProjectNotFoundException;
import com.bugsby.datalayer.service.exceptions.UserNotFoundException;
import com.bugsby.datalayer.service.exceptions.UserNotInProjectException;
import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.Status;
import com.bugsby.datalayer.service.Service;
import com.bugsby.datalayer.controllers.utils.Utils;
import com.bugsby.datalayer.controllers.dtos.requests.AddIssueRequest;
import com.bugsby.datalayer.controllers.dtos.requests.UpdateIssueRequest;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping(value = "/issues")
public class IssueController {
    private final Service service;

    {
        var context = new ClassPathXmlApplicationContext("classpath:spring.xml");
        service = context.getBean(Service.class);
    }

    @PostMapping
    public ResponseEntity<?> addIssue(@RequestBody AddIssueRequest request) {
        try {
            Issue result = service.addIssue(request.toIssue());
            if (result == null) {
                return new ResponseEntity<>("Failed to save issue", HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(IssueDto.from(result), HttpStatus.CREATED);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UserNotInProjectException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (AiServiceException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAssignedIssues(@RequestParam(value = "assignee") String assigneeUsername) {
        try {
            List<Issue> issues = service.getAssignedIssues(assigneeUsername);
            List<IssueDto> result = issues.stream().map(IssueDto::from).collect(Collectors.toList());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getIssueById(@PathVariable Long id) {
        Issue issue = service.getIssueById(id);
        if (issue == null) {
            return new ResponseEntity<>("Issue does not exist", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(IssueDto.from(issue), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteIssue(@PathVariable Long id, @RequestHeader(value = "Authorization") String token) {
        String username = Utils.extractUsername(token);
        try {
            Issue issue = service.deleteIssue(id, username);
            return new ResponseEntity<>(IssueDto.from(issue), HttpStatus.OK);
        } catch (IssueNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UserNotInProjectException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateIssue(@PathVariable Long id, @RequestBody UpdateIssueRequest request, @RequestHeader(value = "Authorization") String token) {
        String username = Utils.extractUsername(token);
        try {
            Issue issue = request.toIssue();
            issue.setId(id);
            Issue result = service.updateIssue(issue, username);
            if (result == null) {
                return new ResponseEntity<>("Failed to update issue", HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(IssueDto.from(result), HttpStatus.OK);
        } catch (IssueNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UserNotInProjectException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/duplicates")
    public ResponseEntity<?> retrieveDuplicateIssues(@RequestBody AddIssueRequest addIssueRequest) {
        try {
            Issue issue = addIssueRequest.toIssue();
            issue.setStatus(Status.TO_DO);

            List<IssueDto> result = service.retrieveDuplicateIssues(issue)
                    .stream()
                    .map(IssueDto::from)
                    .toList();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (ProjectNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AiServiceException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
