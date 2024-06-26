package org.example.testtaskprivatbank.service;

import org.example.testtaskprivatbank.model.Task;
import org.example.testtaskprivatbank.model.TaskStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface TaskService {

    Long createTask(Task task);

    boolean deleteTask(Long id);

    Task updateTaskStatus(Long id, TaskStatus status);

    Task updateTaskFields(Long id, Map<String, Object> taskDetails);

    List<Task> getAllTasks();
}
