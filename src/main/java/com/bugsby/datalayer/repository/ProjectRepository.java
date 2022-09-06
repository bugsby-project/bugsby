package com.bugsby.datalayer.repository;

import com.bugsby.datalayer.model.Project;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProjectRepository extends PagingAndSortingRepository<Project, Long> {
}
