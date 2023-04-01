package com.bugsby.datalayer.repository;

import com.bugsby.datalayer.model.PrefilledIssue;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PrefilledIssueRepository extends PagingAndSortingRepository<PrefilledIssue, Long> {
}
