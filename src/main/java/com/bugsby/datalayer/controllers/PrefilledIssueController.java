package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.model.PrefilledIssue;
import com.bugsby.datalayer.service.Service;
import com.bugsby.datalayer.service.exceptions.PrefilledIssueNotFoundException;
import com.bugsby.datalayer.swagger.model.PrefilledIssueResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;
import java.util.function.Function;

@Controller
@CrossOrigin
public class PrefilledIssueController implements com.bugsby.datalayer.swagger.api.PrefilledIssuesApi {
    private final Service service;
    private final Function<PrefilledIssue, com.bugsby.datalayer.swagger.model.PrefilledIssueResponse> prefilledIssueMapper;

    @Autowired
    public PrefilledIssueController(Service service, Function<PrefilledIssue, PrefilledIssueResponse> prefilledIssueMapper) {
        this.service = service;
        this.prefilledIssueMapper = prefilledIssueMapper;
    }

    @Override
    public ResponseEntity<com.bugsby.datalayer.swagger.model.PrefilledIssueResponse> getPrefilledIssueById(String authorization, Long id) {
        com.bugsby.datalayer.swagger.model.PrefilledIssueResponse prefilledIssueResponse = Optional.ofNullable(service.getPrefilledIssueById(id))
                .map(prefilledIssueMapper)
                .orElseThrow(() -> new PrefilledIssueNotFoundException("No prefilled issue with ID " + id));
        return ResponseEntity.ok(prefilledIssueResponse);
    }
}
