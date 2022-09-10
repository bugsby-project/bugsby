package com.bugsby.datalayer.controllers.dtos;

import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.Severity;
import com.bugsby.datalayer.model.Status;

public record IssueDto(Long id, String title, String description, String expectedBehaviour, String actualBehaviour,
                       String stackTrace, Severity severity, Status status, IssueType type, ProjectDto project,
                       UserDto reporter, UserDto assignee) {
}
