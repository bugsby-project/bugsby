package com.bugsby.datalayer.service.email;

import com.bugsby.datalayer.model.PrefilledIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class BuildFailureEmailBodyBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuildFailureEmailBodyBuilder.class);
    private static final String BUILD_FAILURE_EMAIL_BODY_PATH = "mail/buildFailureEmail.html";
    private static final String TITLE_PLACEHOLDER = "{{title}}";
    private static final String PREFILLED_ISSUE_LINK_PLACEHOLDER = "{{prefilledIssueLink}}";
    private static final String WORKFLOW_RUN_LINK_PLACEHOLDER = "{{workflowRunLink}}";

    private String body;

    @Value("${web.url}")
    private String webUrl;

    public String build(PrefilledIssue prefilledIssue) {
        if (body == null) {
            loadBody();
        }

        return body.replace(TITLE_PLACEHOLDER, prefilledIssue.getTitle())
                .replace(PREFILLED_ISSUE_LINK_PLACEHOLDER, this.generateUrl(prefilledIssue))
                .replace(WORKFLOW_RUN_LINK_PLACEHOLDER, prefilledIssue.getWorkflowRun().getHtmlUrl());
    }

    private void loadBody() {
        try (InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(BUILD_FAILURE_EMAIL_BODY_PATH)) {
            if (inputStream == null){
                throw new IllegalArgumentException(BUILD_FAILURE_EMAIL_BODY_PATH + " not found");
            }

            this.body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Error on loading buildFailureEmail.html file");
            e.printStackTrace();
        }
    }

    private String generateUrl(PrefilledIssue prefilledIssue) {
        return UriComponentsBuilder.fromHttpUrl(webUrl)
                .pathSegment("projects",
                        String.valueOf(prefilledIssue.getProject().getId()),
                        "prefilled-issues",
                        String.valueOf(prefilledIssue.getId()))
                .toUriString();
    }
}
