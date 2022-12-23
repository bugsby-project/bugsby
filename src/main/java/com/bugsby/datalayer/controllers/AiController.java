package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.SeverityLevel;
import com.bugsby.datalayer.service.Service;
import com.bugsby.datalayer.swagger.api.AiApi;
import com.bugsby.datalayer.swagger.model.IssueList;
import com.bugsby.datalayer.swagger.model.IssueRequest;
import com.bugsby.datalayer.swagger.model.IssueResponse;
import com.bugsby.datalayer.swagger.model.IssueTypeResponse;
import com.bugsby.datalayer.swagger.model.SeverityLevelResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.function.Function;

@Controller
@CrossOrigin
public class AiController implements AiApi {
    @Autowired
    private Service service;
    @Autowired
    private Function<IssueRequest, Issue> issueRequestMapper;
    @Autowired
    private Function<Issue, IssueResponse> issueResponseMapper;

    @Override
    public ResponseEntity<SeverityLevelResponse> getSuggestedSeverity(String authorization, String title) {
        SeverityLevel severity = service.predictSeverityLevel(title);
        com.bugsby.datalayer.swagger.model.SeverityLevel severityLevel = com.bugsby.datalayer.swagger.model.SeverityLevel.fromValue(severity.name());
        return ResponseEntity.ok(new SeverityLevelResponse().severity(severityLevel));
    }

    @Override
    public ResponseEntity<IssueTypeResponse> getSuggestedType(String authorization, String title) {
        IssueType issueType = service.predictIssueType(title);
        com.bugsby.datalayer.swagger.model.IssueType type = com.bugsby.datalayer.swagger.model.IssueType.fromValue(issueType.name());
        return ResponseEntity.ok(new IssueTypeResponse().type(type));
    }

    @Override
    public ResponseEntity<IssueList> retrieveDuplicateIssues(String authorization, IssueRequest body) {
        Issue issue = issueRequestMapper.apply(body);
        List<IssueResponse> responses = service.retrieveDuplicateIssues(issue)
                .stream()
                .map(issueResponseMapper)
                .toList();
        IssueList issueList = new IssueList().issues(responses);
        return ResponseEntity.ok(issueList);
    }
}
