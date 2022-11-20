package com.bugsby.datalayer.controllers.mappers;

import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.Role;
import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.swagger.model.ProjectRequest;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;

@Component
public class ProjectRequestMapper implements Function<ProjectRequest, Project> {
    @Override
    public Project apply(ProjectRequest projectRequest) {
        Project project = new Project();
        project.setTitle(projectRequest.getTitle());
        project.setDescription(projectRequest.getDescription());

        Involvement involvement = new Involvement();
        involvement.setProject(project);
        involvement.setUser(new User(projectRequest.getUserId()));
        involvement.setRole(Role.valueOf(projectRequest.getRole().name()));

        project.setInvolvements(Set.of(involvement));
        return project;
    }
}
