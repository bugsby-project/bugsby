package com.bugsby.datalayer.service.mappers;

import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.swagger.ai.model.IssueObject;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class IssueObjectMapper implements Function<Issue, com.bugsby.datalayer.swagger.ai.model.IssueObject> {
    @Override
    public IssueObject apply(Issue issue) {
        return new IssueObject()
                .id(issue.getId())
                .title(issue.getTitle())
                .description(issue.getDescription())
                .severity(com.bugsby.datalayer.swagger.ai.model.SeverityEnum.fromValue(issue.getSeverity().name()))
                .status(com.bugsby.datalayer.swagger.ai.model.StatusEnum.fromValue(issue.getStatus().name()))
                .type(com.bugsby.datalayer.swagger.ai.model.IssueTypeEnum.fromValue(issue.getType().name()));
    }
}
