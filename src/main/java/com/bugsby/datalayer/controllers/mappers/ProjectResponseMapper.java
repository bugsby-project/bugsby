package com.bugsby.datalayer.controllers.mappers;

import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.swagger.model.ProjectResponse;
import org.springframework.stereotype.Component;
import org.threeten.bp.OffsetDateTime;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;

@Component
public class ProjectResponseMapper implements Function<Project, ProjectResponse> {
    @Override
    public ProjectResponse apply(Project project) {
        return new ProjectResponse()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .createdAt(this.convertLocalDateTimeToOffsetDateTime(project.getCreatedAt()));
    }

    private OffsetDateTime convertLocalDateTimeToOffsetDateTime(LocalDateTime time) {
        return OffsetDateTime.parse(time.atOffset(ZoneOffset.UTC).toString());
    }
}
