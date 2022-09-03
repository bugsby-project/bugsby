package com.bugsby.datalayer.validator;

import com.bugsby.datalayer.model.Project;

public class ProjectValidator implements Validator<Long, Project> {
    @Override
    public void validate(Project entity) {
        String errors = "";
        if (!validateTitle(entity))
            errors += ErrorMessages.PROJECT_INVALID_TITLE;
        if (!validateDescription(entity))
            errors += ErrorMessages.PROJECT_INVALID_DESCRIPTION;
        if (!errors.equals(""))
            throw new ValidationException(errors);
    }

    /**
     * Method for verifying if a project's title is not empty
     *
     * @param project: Project, the project to verify the title
     * @return true, if the project's title is not empty
     * false, otherwise
     */
    private boolean validateTitle(Project project) {
        return project.getTitle() != null && !project.getTitle().equals("");
    }

    /**
     * Method for verifying if a project's description is not empty
     *
     * @param project: Project, the project to verify the description
     * @return true, if the project's description is not empty
     * false, otherwise
     */
    private boolean validateDescription(Project project) {
        return project.getDescription() != null && !project.getDescription().equals("");
    }
}
