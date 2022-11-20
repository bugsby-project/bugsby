package com.bugsby.datalayer.controllers.mappers;

import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.swagger.model.InvolvementResponse;
import com.bugsby.datalayer.swagger.model.ProjectResponse;
import com.bugsby.datalayer.swagger.model.Role;
import com.bugsby.datalayer.swagger.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class InvolvementResponseMapper implements Function<Involvement, InvolvementResponse> {
    @Autowired
    private Function<User, UserResponse> userResponseMapper;
    @Autowired
    private Function<Project, ProjectResponse> projectResponseMapper;

    @Override
    public InvolvementResponse apply(Involvement involvement) {
        return new InvolvementResponse()
                .id(involvement.getId())
                .role(Role.fromValue(involvement.getRole().name()))
                .user(userResponseMapper.apply(involvement.getUser()))
                .project(projectResponseMapper.apply(involvement.getProject()));
    }
}
