package com.bugsby.datalayer.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Objects;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Issue implements Entity<Long>, Cloneable {
    private Long id;
    private String title;
    private String description;
    private String expectedBehaviour;
    private String actualBehaviour;
    private String stackTrace;
    private Severity severity;
    private Status status;
    private IssueType type;
    private Project project;
    private User reporter;
    private User assignee;

    public Issue() {
    }

    public Issue(String title, String description, Severity severity, Status status, IssueType type, Project project, User reporter) {
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = status;
        this.type = type;
        this.project = project;
        this.reporter = reporter;
    }

    public Issue(String title, String description, String expectedBehaviour, String actualBehaviour, String stackTrace, Severity severity, Status status, IssueType type, Project project, User reporter) {
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
    }

    public Issue(String title, String description, Severity severity, Status status, IssueType type, Project project, User reporter, User assignee) {
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = status;
        this.type = type;
        this.project = project;
        this.reporter = reporter;
        this.assignee = assignee;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long aLong) {
        this.id = aLong;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpectedBehaviour() {
        return expectedBehaviour;
    }

    public void setExpectedBehaviour(String expectedBehaviour) {
        this.expectedBehaviour = expectedBehaviour;
    }

    public String getActualBehaviour() {
        return actualBehaviour;
    }

    public void setActualBehaviour(String actualBehaviour) {
        this.actualBehaviour = actualBehaviour;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", project=" + project +
                ", reporter=" + reporter +
                ", assignee=" + assignee +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issue issue = (Issue) o;
        return Objects.equals(id, issue.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public Issue clone() {
        try {
            return (Issue) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
