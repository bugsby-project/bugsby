package com.bugsby.datalayer.controllers.dtos.requests;

import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.Severity;
import com.bugsby.datalayer.model.Status;
import com.bugsby.datalayer.model.User;

public record UpdateIssueRequest(String title,
                                 String description,
                                 String expectedBehaviour,
                                 String actualBehaviour,
                                 String stackTrace,
                                 Severity severity,
                                 IssueType type,
                                 Status status,
                                 Long projectId,
                                 Long reporterId,
                                 Long assigneeId) {
    public Issue toIssue() {
        Issue result = new Issue(title, description, expectedBehaviour, actualBehaviour, stackTrace, severity, status, type, new Project(projectId), new User(reporterId));
        if (assigneeId != null) {
            result.setAssignee(new User(assigneeId));
        }

        return result;
    }
}
