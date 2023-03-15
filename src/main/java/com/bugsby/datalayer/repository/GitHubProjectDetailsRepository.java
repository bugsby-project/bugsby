package com.bugsby.datalayer.repository;

import com.bugsby.datalayer.model.GitHubProjectDetails;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GitHubProjectDetailsRepository extends PagingAndSortingRepository<GitHubProjectDetails, Long> {
}
