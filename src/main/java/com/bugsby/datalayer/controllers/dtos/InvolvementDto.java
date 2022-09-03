package com.bugsby.datalayer.controllers.dtos;

import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.Role;

/**
 * This class is intended to fix the infinite recursion JSON mapping error, cause by bidirectional relationships.
 */
public record InvolvementDto(Long id, Role role, UserDto user, ProjectDto project) {
    public static InvolvementDto from(Involvement involvement) {
        UserDto user = UserDto.from(involvement.getUser());
        ProjectDto project = ProjectDto.from(involvement.getProject());
        return new InvolvementDto(involvement.getId(), involvement.getRole(), user, project);
    }
}
