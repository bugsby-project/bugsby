package com.bugsby.datalayer.controllers.dtos;

import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.Project;

import java.time.LocalDateTime;
import java.util.Set;

public record ProjectDto(Long id, String title, String description, LocalDateTime createdAt,
                         Set<Involvement> involvements, Set<Issue> issues) {
    public static ProjectDto from(Project project) {
        // eliminate bidirectional relationship
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
