package com.bugsby.datalayer.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Objects;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Involvement implements Entity<Long>, Cloneable {
    private Long id;
    private Role role;
    private User user;
    private Project project;

    public Involvement() {
    }

    public Involvement(Role role) {
        this.role = role;
    }

    public Involvement(Long id, Role role, User user, Project project) {
        this.id = id;
        this.role = role;
        this.user = user;
        this.project = project;
    }

    public Involvement(Role role, User user, Project project) {
        this.role = role;
        this.user = user;
        this.project = project;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Involvement that = (Involvement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role, user, project);
    }

    @Override
    public String toString() {
        return "Involvement{" +
                "id=" + id +
                ", role=" + role +
                '}';
    }

    @Override
    public Involvement clone() {
        try {
            return (Involvement) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
