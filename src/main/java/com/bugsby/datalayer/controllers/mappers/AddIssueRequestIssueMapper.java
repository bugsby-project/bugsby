package com.bugsby.datalayer.controllers.mappers;

import com.bugsby.datalayer.controllers.dtos.requests.AddIssueRequest;
import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.Status;
import com.bugsby.datalayer.model.User;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
public class AddIssueRequestIssueMapper implements Function<AddIssueRequest, Issue> {
    @Override
    public Issue apply(AddIssueRequest addIssueRequest) {
        Issue result = new Issue(addIssueRequest.title(),
                addIssueRequest.description(),
                addIssueRequest.expectedBehaviour(),
                addIssueRequest.actualBehaviour(),
                addIssueRequest.stackTrace(),
                addIssueRequest.severity(),
                Status.TO_DO,
                addIssueRequest.type(),
                new Project(addIssueRequest.projectId()),
                new User(addIssueRequest.reporterId()));
        result.setAssignee(Optional.ofNullable(addIssueRequest.assigneeId())
                .map(User::new)
                .orElse(null));

        return result;
    }
}
