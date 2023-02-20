package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.service.Service;
import com.bugsby.datalayer.service.exceptions.ProjectNotFoundException;
import com.bugsby.datalayer.swagger.api.ProjectsApi;
import com.bugsby.datalayer.swagger.model.InvolvementResponse;
import com.bugsby.datalayer.swagger.model.InvolvementsList;
import com.bugsby.datalayer.swagger.model.IssueList;
import com.bugsby.datalayer.swagger.model.IssueResponse;
import com.bugsby.datalayer.swagger.model.ProjectRequest;
import com.bugsby.datalayer.swagger.model.ProjectResponse;
import com.bugsby.datalayer.swagger.model.UpdateProjectRequest;
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
    @Autowired
    private Function<Involvement, InvolvementResponse> involvementResponseMapper;
    @Autowired
    private Function<Issue, IssueResponse> issueResponseMapper;

    private static final String PROJECT_DOES_NOT_EXIST_MESSAGE = "Project does not exist";

    @Override
    public ResponseEntity<ProjectResponse> createProject(String authorization, ProjectRequest body) {
        Project project = projectRequestMapper.apply(body);
        project = service.createProject(project);
        ProjectResponse projectResponse = projectResponseMapper.apply(project);
        return new ResponseEntity<>(projectResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<InvolvementsList> getInvolvementsByProjectId(String authorization, Long id) {
        return Optional.ofNullable(service.getProjectById(id))
                .map(project -> project.getInvolvements().stream()
                        .map(involvementResponseMapper)
                        .toList())
                .map(involvementResponses -> new InvolvementsList().involvements(involvementResponses))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ProjectNotFoundException(PROJECT_DOES_NOT_EXIST_MESSAGE));
    }

    @Override
    public ResponseEntity<IssueList> getIssuesByProjectId(String authorization, Long id) {
        return Optional.ofNullable(service.getProjectById(id))
                .map(project -> project.getIssues().stream()
                        .map(issueResponseMapper)
                        .toList())
                .map(issues -> new IssueList().issues(issues))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ProjectNotFoundException(PROJECT_DOES_NOT_EXIST_MESSAGE));
    }

    @Override
    public ResponseEntity<ProjectResponse> getProjectById(String authorization, Long id) {
        return Optional.ofNullable(service.getProjectById(id))
                .map(projectResponseMapper)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ProjectNotFoundException(PROJECT_DOES_NOT_EXIST_MESSAGE));
    }

    @Override
    public ResponseEntity<ProjectResponse> updateProjectById(String authorization, Long id, UpdateProjectRequest updateProjectRequest) {
        Project project = service.updateProject(id, updateProjectRequest);
        ProjectResponse projectResponse = projectResponseMapper.apply(project);
        return ResponseEntity.ok(projectResponse);
    }
}
