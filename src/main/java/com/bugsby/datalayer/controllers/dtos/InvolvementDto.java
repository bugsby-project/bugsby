package com.bugsby.datalayer.controllers.dtos;

import com.bugsby.datalayer.model.Role;

public record InvolvementDto(Long id, Role role, UserDto user, ProjectDto project) {
}
