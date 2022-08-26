package com.bugsby.datalayer.dtos;

import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.Role;

/**
 * This class is intended to fix the infinite recursion JSON mapping error, cause by bidirectional relationships.
 */
public class InvolvementDto {
    private final Long id;
    private final Role role;
    private final UserDto user;
    private final ProjectDto project;

    public InvolvementDto(Long id, Role role, UserDto user, ProjectDto project) {
        this.id = id;
        this.role = role;
        this.user = user;
        this.project = project;
    }

    public Long getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public UserDto getUser() {
        return user;
    }

    public ProjectDto getProject() {
        return project;
    }

    public static InvolvementDto from(Involvement involvement) {
        UserDto user = UserDto.from(involvement.getUser());
        ProjectDto project = ProjectDto.from(involvement.getProject());
        return new InvolvementDto(involvement.getId(), involvement.getRole(), user, project);
    }
}
