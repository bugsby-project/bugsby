package com.bugsby.datalayer.jobs;

import com.bugsby.datalayer.model.WorkflowRun;
import com.bugsby.datalayer.repository.PrefilledIssueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
public class PrefilledIssueCleanUpJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrefilledIssueCleanUpJob.class);
    private static final long WEEKS_TO_DELETE = 2;
    private final PrefilledIssueRepository prefilledIssueRepository;

    @Autowired
    public PrefilledIssueCleanUpJob(PrefilledIssueRepository prefilledIssueRepository) {
        this.prefilledIssueRepository = prefilledIssueRepository;
    }

    @Scheduled(fixedDelay = 7 * WEEKS_TO_DELETE, timeUnit = TimeUnit.DAYS)
    @Transactional
    public void deletePrefilledIssues() {
        LOGGER.info("Deleting PrefilledIssues from {} weeks ago", WEEKS_TO_DELETE);
        LocalDate beforeDate = LocalDate.now().minusWeeks(WEEKS_TO_DELETE);
        prefilledIssueRepository.findAllWithCreationDateBefore(beforeDate)
                .forEach(prefilledIssue -> {
                    // synchronizing relationships
                    WorkflowRun workflowRun = prefilledIssue.getWorkflowRun();
                    prefilledIssue.setWorkflowRun(null);
                    workflowRun.setPrefilledIssue(null);

                    LOGGER.info("Deleting PrefilledIssue with ID {}", prefilledIssue.getId());
                    prefilledIssueRepository.deleteById(prefilledIssue.getId());
                });
    }
}
