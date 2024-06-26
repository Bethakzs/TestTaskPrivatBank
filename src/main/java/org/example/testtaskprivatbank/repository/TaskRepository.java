package org.example.testtaskprivatbank.repository;

import jakarta.transaction.Transactional;
import org.example.testtaskprivatbank.model.Task;
import org.example.testtaskprivatbank.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByTitle(String title);

    List<Task> findByDeadlineBetweenAndStatus(LocalDateTime start, LocalDateTime end, TaskStatus status);

    List<Task> findByDeadlineBeforeAndStatus(LocalDateTime dateTime, TaskStatus status);
//
//    @Query(value = "insert into task (created_at,deadline,description,notified_deadline,notified_one_hour,notified_ten_minutes,status,title) values (:createdAt, :deadline, :description, :notifiedDeadline, :notifiedOneHour, :notifiedTenMinutes, :status, :title)", nativeQuery = true)
//    @Modifying
//    @Transactional
//    void saveTask(@Param("createdAt") LocalDateTime createdAt, @Param("deadline") LocalDateTime deadline, @Param("description") String description, @Param("notifiedDeadline") Boolean notifiedDeadline, @Param("notifiedOneHour") Boolean notifiedOneHour, @Param("notifiedTenMinutes") Boolean notifiedTenMinutes, @Param("status") TaskStatus status, @Param("title") String title);

//    boolean checker1 = task.getDeadline().isBefore(inOneHour);
//    boolean checker2 = task.getDeadline().isBefore(inTenMinutes);
//    boolean checker3 = false;
//
//        taskRepository.saveTask(task.getCreatedAt(),
//                task.getDeadline(),
//                task.getDescription(),
//    checker3,
//    checker2,
//    checker1,
//    TaskStatus.TODO,
//            task.getTitle());
}


