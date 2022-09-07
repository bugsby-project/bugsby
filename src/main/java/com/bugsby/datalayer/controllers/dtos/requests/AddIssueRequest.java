package com.bugsby.datalayer.controllers.dtos.requests;

import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.Severity;

public record AddIssueRequest(String title,
                              String description,
                              String expectedBehaviour,
                              String actualBehaviour,
                              String stackTrace,
                              Severity severity,
                              IssueType type,
                              Long projectId,
                              Long reporterId,
                              Long assigneeId) {
}
