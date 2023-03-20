package com.bugsby.datalayer.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.Objects;

@javax.persistence.Entity
public class GitHubProjectDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "repository_name")
    private String repositoryName;
    @Column(name = "repository_owner")
    private String repositoryOwner;
    @Column(name = "token")
    private String token;
    @OneToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public GitHubProjectDetails() {
    }

    public GitHubProjectDetails(Long id, String repositoryName, String repositoryOwner, String token, Project project) {
        this.id = id;
        this.repositoryName = repositoryName;
        this.repositoryOwner = repositoryOwner;
        this.token = token;
        this.project = project;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long aLong) {
        this.id = aLong;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getRepositoryOwner() {
        return repositoryOwner;
    }

    public void setRepositoryOwner(String repositoryOwner) {
        this.repositoryOwner = repositoryOwner;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitHubProjectDetails that = (GitHubProjectDetails) o;
        return Objects.equals(id, that.id) && Objects.equals(repositoryName, that.repositoryName) && Objects.equals(repositoryOwner, that.repositoryOwner) && Objects.equals(token, that.token) && Objects.equals(project, that.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, repositoryName, repositoryOwner, token, project);
    }

    @Override
    public String toString() {
        return "GitHubProjectDetails{" +
                "id=" + id +
                ", repositoryName='" + repositoryName + '\'' +
                ", repositoryOwner='" + repositoryOwner + '\'' +
                ", token='" + token + '\'' +
                ", project=" + project +
                '}';
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
