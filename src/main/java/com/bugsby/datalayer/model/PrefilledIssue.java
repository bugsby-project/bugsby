package com.bugsby.datalayer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "prefilled_issue")
public class PrefilledIssue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "expected_behaviour")
    private String expectedBehaviour;
    @Column(name = "actual_behaviour", length = 2000)
    private String actualBehaviour;
    @Column(name = "stack_trace", length = 2000)
    private String stackTrace;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "severity")
    private Severity severity;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    private IssueType type;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    @OneToOne(mappedBy = "prefilledIssue")
    private WorkflowRun workflowRun;

    public PrefilledIssue() {
    }

    public PrefilledIssue(Long id, String title, String description, String expectedBehaviour, String actualBehaviour, String stackTrace, Severity severity, IssueType type, Project project, WorkflowRun workflowRun) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.expectedBehaviour = expectedBehaviour;
        this.actualBehaviour = actualBehaviour;
        this.stackTrace = stackTrace;
        this.severity = severity;
        this.type = type;
        this.project = project;
        this.workflowRun = workflowRun;
    }

    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private String expectedBehaviour;
        private String actualBehaviour;
        private String stackTrace;
        private Severity severity;
        private IssueType type;
        private Project project;
        private WorkflowRun workflowRun;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder expectedBehaviour(String expectedBehaviour) {
            this.expectedBehaviour = expectedBehaviour;
            return this;
        }

        public Builder actualBehaviour(String actualBehaviour) {
            this.actualBehaviour = actualBehaviour;
            return this;
        }

        public Builder stackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
            return this;
        }

        public Builder severity(Severity severity) {
            this.severity = severity;
            return this;
        }

        public Builder type(IssueType type) {
            this.type = type;
            return this;
        }

        public Builder project(Project project) {
            this.project = project;
            return this;
        }

        public Builder workflowRun(WorkflowRun workflowRun) {
            this.workflowRun = workflowRun;
            return this;
        }

        public PrefilledIssue build() {
            return new PrefilledIssue(id, title, description, expectedBehaviour, actualBehaviour, stackTrace, severity, type, project, workflowRun);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public WorkflowRun getWorkflowRun() {
        return workflowRun;
    }

    public void setWorkflowRun(WorkflowRun workflowRun) {
        this.workflowRun = workflowRun;
    }
}
