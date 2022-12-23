package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.controllers.utils.Utils;
import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.service.Service;
import com.bugsby.datalayer.service.exceptions.IssueNotFoundException;
import com.bugsby.datalayer.swagger.api.IssuesApi;
import com.bugsby.datalayer.swagger.model.IssueList;
import com.bugsby.datalayer.swagger.model.IssueRequest;
import com.bugsby.datalayer.swagger.model.IssueResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Controller
@CrossOrigin
public class IssueController implements IssuesApi {
    @Autowired
    private Service service;
    @Autowired
    private Function<IssueRequest, Issue> issueRequestMapper;
    @Autowired
    private Function<Issue, IssueResponse> issueResponseMapper;

    @Override
    public ResponseEntity<IssueResponse> addIssue(String authorization, IssueRequest body) {
        Issue issue = issueRequestMapper.apply(body);
        issue = service.addIssue(issue);
        IssueResponse issueResponse = issueResponseMapper.apply(issue);
        return new ResponseEntity<>(issueResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<IssueResponse> deleteIssue(String authorization, Long id) {
        String username = Utils.extractUsername(authorization);
        Issue issue = service.deleteIssue(id, username);
        IssueResponse issueResponse = issueResponseMapper.apply(issue);
        return ResponseEntity.ok(issueResponse);
    }

    @Override
    public ResponseEntity<IssueList> getAssignedIssues(String authorization, String username) {
        List<IssueResponse> responses = service.getAssignedIssues(username)
                .stream()
                .map(issueResponseMapper)
                .toList();
        IssueList issueList = new IssueList().issues(responses);
        return ResponseEntity.ok(issueList);
    }

    @Override
    public ResponseEntity<IssueResponse> getIssueById(String authorization, Long id) {
        IssueResponse response = Optional.ofNullable(service.getIssueById(id))
                .map(issueResponseMapper)
                .orElseThrow(() -> new IssueNotFoundException("Issue does not exist"));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<IssueResponse> updateIssue(String authorization, Long id, IssueRequest body) {
        String username = Utils.extractUsername(authorization);
        Issue issue = issueRequestMapper.apply(body);
        issue.setId(id);

        issue = service.updateIssue(issue, username);
        IssueResponse issueResponse = issueResponseMapper.apply(issue);
        return ResponseEntity.ok(issueResponse);
    }
}
