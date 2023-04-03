package com.bugsby.datalayer.jobs;

import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.PrefilledIssue;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.Severity;
import com.bugsby.datalayer.model.WorkflowRun;
import com.bugsby.datalayer.repository.PrefilledIssueRepository;
import com.bugsby.datalayer.repository.ProjectRepository;
import com.bugsby.datalayer.service.email.EmailService;
import com.bugsby.datalayer.service.exceptions.EmailException;
import com.bugsby.datalayer.swagger.ai.api.DefaultApi;
import com.bugsby.datalayer.swagger.github.api.ActionsApi;
import net.lingala.zip4j.ZipFile;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

@Configuration
@EnableScheduling
public class WorkflowRunJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowRunJob.class);
    private final ProjectRepository projectRepository;
    private final com.bugsby.datalayer.swagger.github.api.ActionsApi actionsApi;
    private final com.bugsby.datalayer.swagger.ai.api.DefaultApi bugsbyAiApi;
    private final PrefilledIssueRepository prefilledIssueRepository;
    private final EmailService emailService;

    @Autowired
    public WorkflowRunJob(ProjectRepository projectRepository, ActionsApi actionsApi, DefaultApi bugsbyAiApi, PrefilledIssueRepository prefilledIssueRepository, EmailService emailService) {
        this.projectRepository = projectRepository;
        this.actionsApi = actionsApi;
        this.bugsbyAiApi = bugsbyAiApi;
        this.prefilledIssueRepository = prefilledIssueRepository;
        this.emailService = emailService;
    }

    @Scheduled(fixedDelay = 3, timeUnit = TimeUnit.MINUTES)
    public void retrieveWorkflows() {
        StreamSupport.stream(projectRepository.findAll().spliterator(), false)
                .filter(this::projectWithGitHubActionsEnabled)
                .map(project -> {
                    actionsApi.getApiClient().setBearerToken(project.getGitHubProjectDetails().getToken());

                    List<com.bugsby.datalayer.swagger.github.model.WorkflowRun> workflowRuns;
                    try {
                        workflowRuns = actionsApi.actionsListWorkflowRunsForRepo(
                                        project.getGitHubProjectDetails().getRepositoryOwner(),
                                        project.getGitHubProjectDetails().getRepositoryName(),
                                        null, null, null, null,
                                        null, null, null,
                                        null, null, null
                                )
                                .getWorkflowRuns();
                    } catch (Exception e) {
                        LOGGER.warn(e.getMessage());
                        workflowRuns = Collections.emptyList();
                    }

                    // retrieve all workflow runs from the API
                    return Pair.with(project, workflowRuns.stream()
                            // filter workflow runs which have already been verified
                            .filter(workflowRun -> project.getWorkflowRuns().stream()
                                    .noneMatch(workflowRunVerified -> workflowRunVerified.getId().equals(workflowRun.getId())))
                            // filter workflow runs which have failed
                            .filter(workflowRun -> "failure".equals(workflowRun.getConclusion()))
                            .toList());
                })
                .filter(pair -> !pair.getValue1().isEmpty())
                .forEach(pair -> {
                    List<PrefilledIssue> prefilledIssues = pair.getValue1().stream()
                            .map(workflowRun -> this.analyseWorkflowLogs(pair.getValue0(), workflowRun))
                            .filter(Objects::nonNull)
                            .map(prefilledIssue -> this.prefilledIssueMapper(pair.getValue0(), prefilledIssue))
                            .map(prefilledIssueRepository::save)
                            .toList();

                    // update workflow runs which have been verified
                    AtomicInteger index = new AtomicInteger(0);
                    List<WorkflowRun> newWorkflowRuns = pair.getValue1().stream()
                            .map(workflowRun -> {
                                WorkflowRun workflowRun1 = this.workflowRunMapper(pair.getValue0(), workflowRun, prefilledIssues.get(index.get()));
                                prefilledIssues.get(index.getAndIncrement()).setWorkflowRun(workflowRun1);
                                return workflowRun1;
                            })
                            .toList();
                    pair.getValue0().getWorkflowRuns().addAll(newWorkflowRuns);
                    projectRepository.save(pair.getValue0());

                    // send emails with the prefilled issues
                    newWorkflowRuns.stream()
                            .map(WorkflowRun::getPrefilledIssue)
                            .forEach(prefilledIssue -> {
                                try {
                                    emailService.sendBuildFailureEmail(prefilledIssue);
                                } catch (EmailException messagingException) {
                                    LOGGER.error("Failed to mail prefilled issue {}", prefilledIssue);
                                }
                            });
                });
    }

    private boolean projectWithGitHubActionsEnabled(Project project) {
        return Optional.ofNullable(project.getGitHubProjectDetails())
                .filter(g -> (g.getToken() != null && !g.getToken().equals("")) ||
                        (g.getRepositoryOwner() != null && !g.getRepositoryOwner().equals("")) ||
                        (g.getRepositoryName() != null && !g.getRepositoryName().equals("")))
                .isPresent();
    }

    private WorkflowRun workflowRunMapper(Project project, com.bugsby.datalayer.swagger.github.model.WorkflowRun workflowRun, PrefilledIssue prefilledIssue) {
        return new WorkflowRun(
                workflowRun.getId(), workflowRun.getName(), workflowRun.getHtmlUrl(), workflowRun.getConclusion(), project, prefilledIssue
        );
    }

    private PrefilledIssue prefilledIssueMapper(Project project, com.bugsby.datalayer.swagger.ai.model.PrefilledIssue prefilledIssue) {
        int maxLength = 2000;
        return new PrefilledIssue.Builder()
                .title(prefilledIssue.getTitle())
                .description(prefilledIssue.getDescription())
                .expectedBehaviour(prefilledIssue.getExpectedBehaviour())
                .actualBehaviour(Optional.ofNullable(prefilledIssue.getActualBehaviour())
                        .filter(string -> string.length() > maxLength)
                        .map(string -> string.substring(0, maxLength))
                        .orElse(null)
                )
                .stackTrace(Optional.ofNullable(prefilledIssue.getStackTrace())
                        .filter(string -> string.length() > maxLength)
                        .map(string -> string.substring(0, maxLength))
                        .orElse(null)
                )
                .severity(Optional.ofNullable(prefilledIssue.getSeverity())
                        .map(com.bugsby.datalayer.swagger.ai.model.SeverityEnum::getValue)
                        .map(Severity::valueOf)
                        .orElse(null))
                .type(Optional.ofNullable(prefilledIssue.getType())
                        .map(com.bugsby.datalayer.swagger.ai.model.IssueTypeEnum::getValue)
                        .map(IssueType::valueOf)
                        .orElse(null))
                .project(project)
                .creationDate(LocalDate.now())
                .build();
    }

    private com.bugsby.datalayer.swagger.ai.model.PrefilledIssue analyseWorkflowLogs(Project project, com.bugsby.datalayer.swagger.github.model.WorkflowRun workflowRun) {
        // download workflow logs
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.fromHttpUrl(actionsApi.getApiClient().getBasePath())
                .path("/repos")
                .path("/" + project.getGitHubProjectDetails().getRepositoryOwner())
                .path("/" + project.getGitHubProjectDetails().getRepositoryName())
                .path("/actions")
                .path("/runs")
                .path("/" + workflowRun.getId())
                .path("/logs")
                .build(false)
                .toString();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + project.getGitHubProjectDetails().getToken());

        HttpEntity<byte[]> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<byte[]> zipBytes = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

        try {
            File zipFile = File.createTempFile("githubLogs", null);
            Files.write(zipFile.toPath(), Objects.requireNonNull(zipBytes.getBody()));

            try (ZipFile zipFileWrapper = new ZipFile(zipFile.getAbsoluteFile())) {
                Path pathDirectoryExtracted = Files.createTempDirectory("githubLogsDirectory");
                zipFileWrapper.extractAll(pathDirectoryExtracted.toString());

                File logFile = Arrays.stream(Objects.requireNonNull(pathDirectoryExtracted.toFile().listFiles()))
                        .filter(File::isFile)
                        .max(Comparator.comparingLong(File::length))
                        .orElseThrow(IOException::new);

                // send log file to analysis
                com.bugsby.datalayer.swagger.ai.model.PrefilledIssue prefilledIssue = bugsbyAiApi.analyseLogFile(workflowRun.getDisplayTitle(), logFile);

                FileSystemUtils.deleteRecursively(pathDirectoryExtracted);
                Files.delete(zipFile.toPath());

                return prefilledIssue;
            }
        } catch (IOException e) {
            return null;
        }
    }
}
