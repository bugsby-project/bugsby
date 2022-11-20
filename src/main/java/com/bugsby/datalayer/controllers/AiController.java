package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.SeverityLevel;
import com.bugsby.datalayer.service.Service;
import com.bugsby.datalayer.swagger.api.AiApi;
import com.bugsby.datalayer.swagger.model.IssueTypeResponse;
import com.bugsby.datalayer.swagger.model.SeverityLevelResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin
public class AiController implements AiApi {
    @Autowired
    private Service service;

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
}
