package com.bugsby.datalayer.validator;

import com.bugsby.datalayer.model.Issue;
import org.springframework.stereotype.Component;

@Component
public class IssueValidator implements Validator<Long, Issue> {
    @Override
    public void validate(Issue entity) {
        StringBuilder builder = new StringBuilder();
        if (!validateTitle(entity)) {
            builder.append(ErrorMessages.ISSUE_INVALID_TITLE);
        }
        if (!validateSeverity(entity)) {
            builder.append(ErrorMessages.ISSUE_INVALID_SEVERITY);
        }
        if (!validateStatus(entity)) {
            builder.append(ErrorMessages.ISSUE_INVALID_STATUS);
        }
        if (!validateType(entity)) {
            builder.append(ErrorMessages.ISSUE_INVALID_TYPE);
        }
        if (!validateProject(entity)) {
            builder.append(ErrorMessages.ISSUE_INVALID_PROJECT);
        }
        if (!validateReporter(entity)) {
            builder.append(ErrorMessages.ISSUE_INVALID_REPORTER);
        }

        String errors = builder.toString();
        if (!errors.equals("")) {
            throw new ValidationException(errors);
        }
    }

    private boolean validateTitle(Issue issue) {
        return !issue.getTitle().equals("");
    }

    private boolean validateSeverity(Issue issue) {
        return issue.getSeverity() != null;
    }

    private boolean validateStatus(Issue issue) {
        return issue.getStatus() != null;
    }

    private boolean validateType(Issue issue) {
        return issue.getType() != null;
    }

    private boolean validateProject(Issue issue) {
        return issue.getProject() != null;
    }

    private boolean validateReporter(Issue issue) {
        return issue.getReporter() != null;
    }
}
