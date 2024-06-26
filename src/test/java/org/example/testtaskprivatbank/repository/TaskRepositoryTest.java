package org.example.testtaskprivatbank.repository;

import org.example.testtaskprivatbank.TestTaskPrivatBankApplication;
import org.example.testtaskprivatbank.model.Task;
import org.example.testtaskprivatbank.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = TestTaskPrivatBankApplication.class)
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void testExistsByTitle() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setCreatedAt(LocalDateTime.now());
        task.setDeadline(LocalDateTime.now().plusDays(1));
        task.setStatus(TaskStatus.TODO);
        taskRepository.save(task);

        boolean exists = taskRepository.existsByTitle("Test Task");

        assertThat(exists).isTrue();
    }

    @Test
    public void testFindByDeadlineBetweenAndStatus() {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setCreatedAt(LocalDateTime.now());
        task1.setDeadline(LocalDateTime.now().plusHours(2));
        task1.setStatus(TaskStatus.TODO);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setCreatedAt(LocalDateTime.now());
        task2.setDeadline(LocalDateTime.now().plusHours(3));
        task2.setStatus(TaskStatus.TODO);
        taskRepository.save(task2);

        LocalDateTime now = LocalDateTime.now();
        List<Task> tasks = taskRepository.findByDeadlineBetweenAndStatus(now.plusHours(1), now.plusHours(4), TaskStatus.TODO);

        assertThat(tasks).contains(task1, task2);
    }

    @Test
    public void testFindByDeadlineBeforeAndStatus() {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setCreatedAt(LocalDateTime.now());
        task1.setDeadline(LocalDateTime.now().minusHours(2));
        task1.setStatus(TaskStatus.TODO);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setCreatedAt(LocalDateTime.now());
        task2.setDeadline(LocalDateTime.now().minusHours(3));
        task2.setStatus(TaskStatus.TODO);
        taskRepository.save(task2);

        List<Task> tasks = taskRepository.findByDeadlineBeforeAndStatus(LocalDateTime.now(), TaskStatus.TODO);

        assertThat(tasks).contains(task1, task2);
    }
}
