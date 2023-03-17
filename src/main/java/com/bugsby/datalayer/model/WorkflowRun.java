package com.bugsby.datalayer.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@javax.persistence.Entity
public class WorkflowRun {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "html_url")
    private String htmlUrl;
    @Column(name = "conclusion")
    private String conclusion;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public WorkflowRun() {
    }

    public WorkflowRun(Long id, String name, String htmlUrl, String conclusion, Project project) {
        this.id = id;
        this.name = name;
        this.htmlUrl = htmlUrl;
        this.conclusion = conclusion;
        this.project = project;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long aLong) {
        this.id = aLong;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkflowRun that = (WorkflowRun) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(htmlUrl, that.htmlUrl) && Objects.equals(conclusion, that.conclusion) && Objects.equals(project, that.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, htmlUrl, conclusion, project);
    }

    @Override
    public String toString() {
        return "WorkflowRun{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", conclusion='" + conclusion + '\'' +
                ", project=" + project +
                '}';
    }
}
