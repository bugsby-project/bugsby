package com.bugsby.datalayer.controllers.mappers;

import com.bugsby.datalayer.controllers.dtos.requests.IssueRequest;
import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.Status;
import com.bugsby.datalayer.model.User;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
public class IssueRequestMapper implements Function<IssueRequest, Issue> {
    @Override
    public Issue apply(IssueRequest issueRequest) {
        Issue result = new Issue(issueRequest.title(),
                issueRequest.description(),
                issueRequest.expectedBehaviour(),
                issueRequest.actualBehaviour(),
                issueRequest.stackTrace(),
                issueRequest.severity(),
                Optional.ofNullable(issueRequest.status()).orElse(Status.TO_DO),
                issueRequest.type(),
                new Project(issueRequest.projectId()),
                new User(issueRequest.reporterId()));
        result.setAssignee(Optional.ofNullable(issueRequest.assigneeId())
                .map(User::new)
                .orElse(null));

        return result;
    }
}
