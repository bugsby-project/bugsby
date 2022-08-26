package com.bugsby.datalayer.ai;

import com.bugsby.datalayer.exceptions.AiServiceException;
import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.ProfanityLevel;
import com.bugsby.datalayer.model.SeverityLevel;

import java.util.List;

public interface Predictor {
    /**
     * Method for predicting the severity level of an issue, based on its title
     *
     * @param title, the title of the issue to predict the severity level to
     * @return the predicted severity level
     * @throws AiServiceException if any errors occur during the prediction process
     */
    SeverityLevel predictSeverityLevel(String title) throws AiServiceException;

    /**
     * Method for predicting the label (type) of an issue, based on its title
     *
     * @param title, the title of the issue to predict the type to
     * @return the predicted issue type
     * @throws AiServiceException if any errors occur during the prediction process
     */
    IssueType predictIssueType(String title) throws AiServiceException;

    /**
     * Method for identifying the profanity level of a piece of text
     *
     * @param text, the text to analyse
     * @return the predicted profanity level
     * @throws AiServiceException if any errors occur during the prediction process
     */
    ProfanityLevel predictProfanityLevel(String text) throws AiServiceException;

    /**
     * Method for retrieving the duplicate issues in a project, given a not-yet-added issue
     *
     * @param projectIssues, all the issues of the respective project
     * @param issue,         the issue that is wished to be compared against {@code projectIssues}
     * @return a {@code List} containing the possible duplicates of {@code issue}, members of {@code projectIssues}
     * @throws AiServiceException if any errors occur during the process
     */
    List<Issue> detectDuplicateIssues(List<Issue> projectIssues, Issue issue) throws AiServiceException;
}
