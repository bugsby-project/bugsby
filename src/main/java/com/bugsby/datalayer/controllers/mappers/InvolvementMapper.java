package com.bugsby.datalayer.controllers.mappers;

import com.bugsby.datalayer.controllers.dtos.InvolvementDto;
import com.bugsby.datalayer.controllers.dtos.ProjectDto;
import com.bugsby.datalayer.controllers.dtos.UserDto;
import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class InvolvementMapper implements Function<Involvement, InvolvementDto> {
    @Autowired
    private Function<User, UserDto> userMapper;
    @Autowired
    private Function<Project, ProjectDto> projectMapper;

    @Override
    public InvolvementDto apply(Involvement involvement) {
        UserDto user = userMapper.apply(involvement.getUser());
        ProjectDto project = projectMapper.apply(involvement.getProject());
        return new InvolvementDto(involvement.getId(), involvement.getRole(), user, project);
    }
}
