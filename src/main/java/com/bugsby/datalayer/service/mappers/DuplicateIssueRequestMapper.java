package com.bugsby.datalayer.service.mappers;

import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.swagger.ai.model.DuplicateIssuesRequest;
import com.bugsby.datalayer.swagger.ai.model.IssueObject;
import com.bugsby.datalayer.swagger.ai.model.IssueObjectList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
public class DuplicateIssueRequestMapper implements BiFunction<List<Issue>, Issue, com.bugsby.datalayer.swagger.ai.model.DuplicateIssuesRequest> {
    private final Function<Issue, com.bugsby.datalayer.swagger.ai.model.IssueObject> issueObjectMapper;

    @Autowired
    public DuplicateIssueRequestMapper(Function<Issue, IssueObject> issueObjectMapper) {
        this.issueObjectMapper = issueObjectMapper;
    }

    @Override
    public DuplicateIssuesRequest apply(List<Issue> issues, Issue issue) {
        com.bugsby.datalayer.swagger.ai.model.IssueObjectList issueObjectList = new IssueObjectList()
                .issues(issues.stream()
                        .map(issueObjectMapper)
                        .toList());
        return new DuplicateIssuesRequest()
                .issue(issueObjectMapper.apply(issue))
                .projectIssues(issueObjectList);
    }
}
