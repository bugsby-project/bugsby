package com.bugsby.datalayer.repository;

import com.bugsby.datalayer.model.PrefilledIssue;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PrefilledIssueRepository extends PagingAndSortingRepository<PrefilledIssue, Long> {
    @Query("select i from PrefilledIssue i where i.creationDate <= :creationDate")
    List<PrefilledIssue> findAllWithCreationDateBefore(@Param("creationDate") LocalDate creationDate);
}
