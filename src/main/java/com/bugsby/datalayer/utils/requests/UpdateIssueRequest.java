package com.bugsby.datalayer.utils.requests;

import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.Severity;
import com.bugsby.datalayer.model.Status;
import com.bugsby.datalayer.model.User;

public class UpdateIssueRequest {
    private String title;
    private String description;
    private String expectedBehaviour;
    private String actualBehaviour;
    private String stackTrace;
    private Severity severity;
    private IssueType type;
    private Status status;
    private Long projectId;
    private Long reporterId;
    private Long assigneeId;

    public UpdateIssueRequest() {
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

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    @Override
    public String toString() {
        return "UpdateIssueRequest{" +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", expectedBehaviour='" + expectedBehaviour + '\'' +
                ", actualBehaviour='" + actualBehaviour + '\'' +
                ", stackTrace='" + stackTrace + '\'' +
                ", severity=" + severity +
                ", type=" + type +
                ", status=" + status +
                ", projectId=" + projectId +
                ", reporterId=" + reporterId +
                ", assigneeId=" + assigneeId +
                '}';
    }

    public Issue toIssue() {
        Issue result = new Issue(title, description, expectedBehaviour, actualBehaviour, stackTrace, severity, status, type, new Project(projectId), new User(reporterId));
        if (assigneeId != null) {
            result.setAssignee(new User(assigneeId));
        }

        return result;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
