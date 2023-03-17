package com.bugsby.datalayer.service.validators;

import com.bugsby.datalayer.model.GitHubProjectDetails;
import com.bugsby.datalayer.service.exceptions.GitHubProjectDetailsException;
import com.bugsby.datalayer.swagger.github.api.ReposApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

@Component
public class GitHubProjectDetailsValidator {
    private final com.bugsby.datalayer.swagger.github.api.ReposApi reposApi;

    @Autowired
    public GitHubProjectDetailsValidator(ReposApi reposApi) {
        this.reposApi = reposApi;
    }

    public void validate(GitHubProjectDetails gitHubProjectDetails) {
        Optional.ofNullable(gitHubProjectDetails)
                .filter(g -> (g.getToken() != null && !g.getToken().equals("")) ||
                        (g.getRepositoryOwner() != null && !g.getRepositoryOwner().equals("")) ||
                        (g.getRepositoryName() != null && !g.getRepositoryName().equals("")))
                .ifPresent(details -> {
                    try {
                        reposApi.getApiClient().setBearerToken(details.getToken());
                        reposApi.reposGet(details.getRepositoryOwner(), details.getRepositoryName());
                    } catch (HttpClientErrorException e) {
                        String message;
                        switch (e.getStatusCode()) {
                            case UNAUTHORIZED -> message = "Token not valid";
                            case NOT_FOUND -> message = "Repository owner or name not found";
                            default -> message = "Error with GitHub details";
                        }
                        throw new GitHubProjectDetailsException(message);
                    }
                });
    }
}
