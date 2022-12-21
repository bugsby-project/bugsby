package com.bugsby.datalayer.controllers.mappers;

import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.Severity;
import com.bugsby.datalayer.model.Status;
import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.swagger.model.IssueRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
public class IssueRequestMapper implements Function<IssueRequest, Issue> {
    @Override
    public Issue apply(IssueRequest issueRequest) {
        Issue result = new Issue();
        result.setTitle(issueRequest.getTitle());
        result.setDescription(issueRequest.getDescription());
        result.setExpectedBehaviour(issueRequest.getExpectedBehaviour());
        result.setActualBehaviour(issueRequest.getActualBehaviour());
        result.setStackTrace(issueRequest.getStackTrace());
        result.setSeverity(Severity.valueOf(issueRequest.getSeverity().name()));
        result.setType(IssueType.valueOf(issueRequest.getType().name()));
        result.setStatus(Status.valueOf(Optional.ofNullable(issueRequest.getStatus())
                .orElse(com.bugsby.datalayer.swagger.model.Status.TO_DO).name()));
        result.setProject(new Project(issueRequest.getProjectId()));
        result.setReporter(new User(issueRequest.getReporterId()));

        result.setAssignee(Optional.ofNullable(issueRequest.getAssigneeId())
                .filter(id -> id > 0)
                .map(User::new)
                .orElse(null));
        return result;
    }
}
