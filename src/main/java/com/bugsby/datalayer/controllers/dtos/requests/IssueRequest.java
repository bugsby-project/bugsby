package com.bugsby.datalayer.controllers.dtos.requests;

import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.Severity;
import com.bugsby.datalayer.model.Status;

public record IssueRequest(String title,
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
}
