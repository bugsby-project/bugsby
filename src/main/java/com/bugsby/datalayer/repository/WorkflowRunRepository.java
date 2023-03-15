package com.bugsby.datalayer.repository;

import com.bugsby.datalayer.model.WorkflowRun;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface WorkflowRunRepository extends PagingAndSortingRepository<WorkflowRun, Long> {
}
