package com.bugsby.datalayer.controllers.mappers;

import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.swagger.model.IssueResponse;
import com.bugsby.datalayer.swagger.model.IssueType;
import com.bugsby.datalayer.swagger.model.Severity;
import com.bugsby.datalayer.swagger.model.SeverityLevel;
import com.bugsby.datalayer.swagger.model.Status;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
public class IssueResponseMapper implements Function<Issue, IssueResponse> {
    @Override
    public IssueResponse apply(Issue issue) {
        return new IssueResponse()
                .id(issue.getId())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .expectedBehaviour(issue.getExpectedBehaviour())
                .actualBehaviour(issue.getActualBehaviour())
                .stackTrace(issue.getStackTrace())
                .severity(Severity.fromValue(issue.getSeverity().name()))
                .type(IssueType.fromValue(issue.getType().name()))
                .status(Status.fromValue(issue.getStatus().name()))
                .projectId(issue.getProject().getId())
                .reporterId(issue.getReporter().getId())
                .assigneeId(Optional.ofNullable(issue.getAssignee())
                        .map(User::getId)
                        .orElse(0L));
    }
}
