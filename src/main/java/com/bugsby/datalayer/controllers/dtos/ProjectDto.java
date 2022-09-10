package com.bugsby.datalayer.controllers.dtos;

import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.Issue;

import java.time.LocalDateTime;
import java.util.Set;

public record ProjectDto(Long id, String title, String description, LocalDateTime createdAt,
                         Set<Involvement> involvements, Set<Issue> issues) {
}
