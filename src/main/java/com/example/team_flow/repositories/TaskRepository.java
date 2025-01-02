package com.example.team_flow.repositories;

import com.example.team_flow.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find tasks by title containing the search query (case insensitive), with pagination.
     *
     * @param title    the search query
     * @param pageable the pagination information
     * @return a page of tasks matching the search query
     */
    Page<Task> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
