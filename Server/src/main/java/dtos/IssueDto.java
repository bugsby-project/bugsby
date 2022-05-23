package dtos;

import model.Issue;
import model.IssueType;
import model.Severity;
import model.Status;

import java.util.Collections;

public class IssueDto {
    private final Long id;
    private final String title;
    private final String description;
    private final String expectedBehaviour;
    private final String actualBehaviour;
    private final String stackTrace;
    private final Severity severity;
    private final Status status;
    private final IssueType type;
    private final ProjectDto project;
    private final UserDto reporter;
    private final UserDto assignee;

    public IssueDto(Long id, String title, String description, String expectedBehaviour, String actualBehaviour, String stackTrace, Severity severity, Status status, IssueType type, ProjectDto project, UserDto reporter, UserDto assignee) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.expectedBehaviour = expectedBehaviour;
        this.actualBehaviour = actualBehaviour;
        this.stackTrace = stackTrace;
        this.severity = severity;
        this.status = status;
        this.type = type;
        this.project = project;
        this.reporter = reporter;
        this.assignee = assignee;
    }

    public static IssueDto from(Issue issue) {
        UserDto reporter = UserDto.from(issue.getReporter());
        UserDto assignee = issue.getAssignee() == null ? null : UserDto.from(issue.getAssignee());

        issue.getProject().setIssues(Collections.emptySet());
        issue.getProject().setInvolvements(Collections.emptySet());
        ProjectDto project = ProjectDto.from(issue.getProject());

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

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getExpectedBehaviour() {
        return expectedBehaviour;
    }

    public String getActualBehaviour() {
        return actualBehaviour;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Status getStatus() {
        return status;
    }

    public IssueType getType() {
        return type;
    }

    public ProjectDto getProject() {
        return project;
    }

    public UserDto getReporter() {
        return reporter;
    }

    public UserDto getAssignee() {
        return assignee;
    }
}
