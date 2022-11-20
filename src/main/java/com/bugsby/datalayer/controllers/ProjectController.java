package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.service.Service;
import com.bugsby.datalayer.service.exceptions.ProjectNotFoundException;
import com.bugsby.datalayer.swagger.api.ProjectsApi;
import com.bugsby.datalayer.swagger.model.ProjectRequest;
import com.bugsby.datalayer.swagger.model.ProjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;
import java.util.function.Function;

@Controller
@CrossOrigin
public class ProjectController implements ProjectsApi {
    @Autowired
    private Service service;
    @Autowired
    private Function<Project, ProjectResponse> projectResponseMapper;
    @Autowired
    private Function<ProjectRequest, Project> projectRequestMapper;

    @Override
    public ResponseEntity<ProjectResponse> createProject(String authorization, ProjectRequest body) {
        Project project = projectRequestMapper.apply(body);
        project = service.createProject(project);
        ProjectResponse projectResponse = projectResponseMapper.apply(project);
        return new ResponseEntity<>(projectResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ProjectResponse> getProjectById(String authorization, Long id) {
        return Optional.ofNullable(service.getProjectById(id))
                .map(projectResponseMapper)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ProjectNotFoundException("Project does not exist"));
    }
}
