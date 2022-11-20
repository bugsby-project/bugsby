package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.Role;
import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.service.Service;
import com.bugsby.datalayer.swagger.api.InvolvementsApi;
import com.bugsby.datalayer.swagger.model.InvolvementResponse;
import com.bugsby.datalayer.swagger.model.InvolvementsList;
import com.bugsby.datalayer.swagger.model.InvolvementsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.function.Function;

@Controller
@CrossOrigin
public class InvolvementController implements InvolvementsApi {
    @Autowired
    private Service service;
    @Autowired
    private Function<Involvement, InvolvementResponse> involvementResponseMapper;

    @Override
    public ResponseEntity<InvolvementResponse> addParticipant(String authorization, InvolvementsRequest body) {
        Project project = new Project(body.getProjectId());
        User participant = new User(body.getUsername());
        User requester = new User(body.getRequesterId());
        Role role = Role.valueOf(body.getRole().name());
        Involvement involvement = new Involvement(role, participant, project);

        Involvement result = service.addParticipant(involvement, requester);
        InvolvementResponse response = involvementResponseMapper.apply(result);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<InvolvementsList> getInvolvementsByUsername(String authorization, String username) {
        List<InvolvementResponse> responses = service.getInvolvementsByUsername(username).stream()
                .map(involvementResponseMapper)
                .toList();
        InvolvementsList involvementsList = new InvolvementsList().involvements(responses);
        return ResponseEntity.ok(involvementsList);
    }
}
