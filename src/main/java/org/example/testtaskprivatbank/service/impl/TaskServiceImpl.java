package org.example.testtaskprivatbank.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.testtaskprivatbank.model.Task;
import org.example.testtaskprivatbank.model.TaskStatus;
import org.example.testtaskprivatbank.repository.TaskRepository;
import org.example.testtaskprivatbank.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public Long createTask(Task task) {
        if (task.getTitle().isEmpty()) {
            String errorMessage = "Task title cannot be empty.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        if (taskRepository.existsByTitle(task.getTitle())) {
            String errorMessage = "Task with title '" + task.getTitle() + "' already exists.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        if (taskRepository.count() >= 100) {
            String errorMessage = "Maximum number of tasks reached.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        LocalDateTime now = LocalDateTime.now();
        task.setCreatedAt(now);
        task.setStatus(TaskStatus.TODO);

        // Set start time for notifications
        LocalDateTime inOneHour = now.plusHours(1);
        LocalDateTime inTenMinutes = now.plusMinutes(10);

        // check if the deadline is before the notification time
        task.setNotifiedOneHour(task.getDeadline().isBefore(inOneHour));
        task.setNotifiedTenMinutes(task.getDeadline().isBefore(inTenMinutes));
        task.setNotifiedDeadline(false);

        Task savedTask = taskRepository.save(task);
        logger.info("Created task with id: {}", savedTask.getId());
        return savedTask.getId();
    }

    @Override
    @Transactional
    public boolean deleteTask(Long id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            taskRepository.deleteById(id);
            logger.info("Deleted task with id: {}", id);
            return true;
        }
        logger.warn("Task with id {} not found for deletion", id);
        return false;
    }

    @Override
    @Transactional
    public Task updateTaskStatus(Long id, TaskStatus status) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setStatus(status);
            Task updatedTask = taskRepository.save(task);
            logger.info("Updated status for task with id {}. New status: {}", id, updatedTask.getStatus());
            return updatedTask;
        }
        logger.warn("Task with id {} not found for status update", id);
        return null;
    }

    @Override
    @Transactional
    public Task updateTaskFields(Long id, Map<String, Object> taskDetails) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();

            // Validate and update fields if they are present in the request body
            if (taskDetails.containsKey("title")) {
                String title = (String) taskDetails.get("title");
                if (taskRepository.existsByTitle(title) && !task.getTitle().equals(title)) {
                    throw new IllegalArgumentException("A task with the given title already exists.");
                }
                task.setTitle(title);
            }
            if (taskDetails.containsKey("description")) {
                String description = (String) taskDetails.get("description");
                task.setDescription(description);
            }
            if (taskDetails.containsKey("status")) {
                String status = (String) taskDetails.get("status");
                task.setStatus(TaskStatus.valueOf(status));
            }
            if (taskDetails.containsKey("deadline")) {
                String deadlineStr = (String) taskDetails.get("deadline");
                LocalDateTime deadline = LocalDateTime.parse(deadlineStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                task.setDeadline(deadline);

                // Check and set notification fields based on the new deadline
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime inOneHour = now.plusHours(1);
                LocalDateTime inTenMinutes = now.plusMinutes(10);

                task.setNotifiedOneHour(deadline.isBefore(inOneHour));
                task.setNotifiedTenMinutes(deadline.isBefore(inTenMinutes));
                task.setNotifiedDeadline(false);
            }

            Task updatedTask = taskRepository.save(task);
            return updatedTask;
        }
        return null;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        logger.info("Retrieved {} tasks", tasks.size());
        return tasks;
    }
}