package com.bugsby.datalayer.controllers.mappers;

import com.bugsby.datalayer.controllers.dtos.ProjectDto;
import com.bugsby.datalayer.model.Project;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ProjectMapper implements Function<Project, ProjectDto> {
    @Override
    public ProjectDto apply(Project project) {
        project.getInvolvements()
                .forEach(involvement -> {
                    involvement.setProject(null);
                    involvement.getUser().setInvolvements(null);
                    involvement.getUser().setAssignedIssues(null);
                });

        project.getIssues()
                .forEach(issue -> {
                    issue.setProject(null);
                    issue.getReporter().setAssignedIssues(null);
                    issue.getReporter().setInvolvements(null);
                    if (issue.getAssignee() != null) {
                        issue.getAssignee().setAssignedIssues(null);
                        issue.getAssignee().setInvolvements(null);
                    }
                });

        return new ProjectDto(project.getId(), project.getTitle(), project.getDescription(), project.getCreatedAt(), project.getInvolvements(), project.getIssues());
    }
}
