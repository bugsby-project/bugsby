package com.bugsby.datalayer.controllers.dtos;

import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.Severity;
import com.bugsby.datalayer.model.Status;

import java.util.Collections;

public record IssueDto(Long id, String title, String description, String expectedBehaviour, String actualBehaviour,
                       String stackTrace, Severity severity, Status status, IssueType type, ProjectDto project,
                       UserDto reporter, UserDto assignee) {
    public static IssueDto from(Issue issue) {
        UserDto reporter = UserDto.from(issue.getReporter());
        UserDto assignee = issue.getAssignee() == null ? null : UserDto.from(issue.getAssignee());

        issue.getProject().setIssues(Collections.emptySet());
        issue.getProject().setInvolvements(Collections.emptySet());
        ProjectDto project = ProjectDto.from(issue.getProject());

        return new IssueDto(issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getExpectedBehaviour(),
                issue.getActualBehaviour(),
                issue.getStackTrace(),
                issue.getSeverity(),
                issue.getStatus(),
                issue.getType(),
                project,
                reporter,
                assignee
        );
    }
}
