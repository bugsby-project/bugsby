package com.bugsby.datalayer.controllers.mappers;

import com.bugsby.datalayer.model.PrefilledIssue;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PrefilledIssueMapper implements Function<PrefilledIssue, com.bugsby.datalayer.swagger.model.PrefilledIssueResponse> {
    @Override
    public com.bugsby.datalayer.swagger.model.PrefilledIssueResponse apply(PrefilledIssue prefilledIssue) {
        return new com.bugsby.datalayer.swagger.model.PrefilledIssueResponse()
                .id(prefilledIssue.getId())
                .title(prefilledIssue.getTitle())
                .description(prefilledIssue.getDescription())
                .expectedBehaviour(prefilledIssue.getExpectedBehaviour())
                .actualBehaviour(prefilledIssue.getActualBehaviour())
                .stackTrace(prefilledIssue.getStackTrace())
                .severity(com.bugsby.datalayer.swagger.model.Severity.fromValue(prefilledIssue.getSeverity().name()))
                .type(com.bugsby.datalayer.swagger.model.IssueType.fromValue(prefilledIssue.getType().name()))
                .projectId(prefilledIssue.getProject().getId());
    }
}
