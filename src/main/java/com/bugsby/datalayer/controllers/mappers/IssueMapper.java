package com.bugsby.datalayer.controllers.mappers;

import com.bugsby.datalayer.controllers.dtos.IssueDto;
import com.bugsby.datalayer.controllers.dtos.ProjectDto;
import com.bugsby.datalayer.controllers.dtos.UserDto;
import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

@Component
public class IssueMapper implements Function<Issue, IssueDto> {
    @Autowired
    private Function<User, UserDto> userMapper;
    @Autowired
    private Function<Project, ProjectDto> projectMapper;

    @Override
    public IssueDto apply(Issue issue) {
        UserDto reporter = userMapper.apply(issue.getReporter());
        UserDto assignee = Optional.ofNullable(issue.getAssignee())
                .map(userMapper)
                .orElse(null);

        issue.getProject().setIssues(Collections.emptySet());
        issue.getProject().setInvolvements(Collections.emptySet());
        ProjectDto project = projectMapper.apply(issue.getProject());

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
