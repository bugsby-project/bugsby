package com.bugsby.datalayer.service.email;

import com.bugsby.datalayer.model.PrefilledIssue;
import com.bugsby.datalayer.service.exceptions.EmailException;

public interface EmailService {
    /**
     * Method for sending an email about a build failure with a prefilled issue report to all project participants
     * @param prefilledIssue, the prefilled issue to send an email about
     */
    void sendBuildFailureEmail(PrefilledIssue prefilledIssue) throws EmailException;
}
