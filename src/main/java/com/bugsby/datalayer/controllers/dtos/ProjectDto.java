package com.bugsby.datalayer.controllers.dtos;

import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.Project;

import java.time.LocalDateTime;
import java.util.Set;

public class ProjectDto {
    private final Long id;
    private final String title;
    private final String description;
    private final LocalDateTime createdAt;
    private final Set<Involvement> involvements;
    private final Set<Issue> issues;

    public ProjectDto(Long id, String title, String description, LocalDateTime createdAt, Set<Involvement> involvements, Set<Issue> issues) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.involvements = involvements;
        this.issues = issues;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<Involvement> getInvolvements() {
        return involvements;
    }

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

    public Set<Issue> getIssues() {
        return issues;
    }
}
