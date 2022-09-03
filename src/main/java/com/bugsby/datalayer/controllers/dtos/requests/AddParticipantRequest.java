package com.bugsby.datalayer.controllers.dtos.requests;

import com.bugsby.datalayer.model.Role;

public record AddParticipantRequest(Long projectId,
                                    Long requesterId,
                                    String username,
                                    Role role) {
}