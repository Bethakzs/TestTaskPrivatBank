package org.example.testtaskprivatbank.repository;

import org.example.testtaskprivatbank.model.Task;
import org.example.testtaskprivatbank.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByTitle(String title);

    List<Task> findByDeadlineBetweenAndStatus(LocalDateTime start, LocalDateTime end, TaskStatus status);

    List<Task> findByDeadlineBeforeAndStatus(LocalDateTime dateTime, TaskStatus status);
}


