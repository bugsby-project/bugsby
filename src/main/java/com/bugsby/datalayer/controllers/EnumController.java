package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.Role;
import com.bugsby.datalayer.model.Severity;
import com.bugsby.datalayer.model.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class EnumController {
    @GetMapping(value = "/issue-types")
    public ResponseEntity<IssueType[]> getIssueTypes() {
        return new ResponseEntity<>(IssueType.values(), HttpStatus.OK);
    }

    @GetMapping(value = "/roles")
    public ResponseEntity<Role[]> getRoles() {
        return new ResponseEntity<>(Role.values(), HttpStatus.OK);
    }

    @GetMapping(value = "/severities")
    public ResponseEntity<Severity[]> getSeverities() {
        return new ResponseEntity<>(Severity.values(), HttpStatus.OK);
    }

    @GetMapping(value = "/statuses")
    public ResponseEntity<Status[]> getStatuses() {
        return new ResponseEntity<>(Status.values(), HttpStatus.OK);
    }
}
