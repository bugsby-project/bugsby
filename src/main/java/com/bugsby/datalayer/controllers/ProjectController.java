package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.controllers.dtos.ProjectDto;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.service.Service;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping(value = "/projects")
public class ProjectController {
    private final Service service;

    {
        var context = new ClassPathXmlApplicationContext("classpath:spring.xml");
        service = context.getBean(Service.class);
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Project project) {
        try {
            Project result = service.createProject(project);
            if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Failed to create project", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable long id) {
        Project result = service.getProjectById(id);
        if (result == null) {
            return new ResponseEntity<>("Project does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ProjectDto.from(result), HttpStatus.OK);
    }
}
